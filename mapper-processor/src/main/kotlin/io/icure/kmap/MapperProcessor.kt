package io.icure.kmap

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import java.io.OutputStream
import java.util.*

fun OutputStream.appendText(str: String) {
    this.write(str.toByteArray())
}

@KotlinPoetKspPreview
@KspExperimental
class MapperProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger
) : SymbolProcessor {
    val typeConverters: Map<Pair<String, String>, CodeBlock> = mapOf()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("org.mapstruct.Mapper")
        val invalidSymbols = symbols.filter { !it.validate() }.toList()
        symbols
            .mapNotNull { (it as? KSClassDeclaration)?.takeIf { it.validate() } }
            .filter { cd ->
                cd.classKind == ClassKind.INTERFACE || cd.classKind == ClassKind.CLASS && cd.modifiers.contains(
                    Modifier.ABSTRACT
                )
            }
            .forEach {
                it.accept(MapperVisitor(it.annotations.find {
                    it.annotationType.resolve().let {
                        it.declaration.packageName.asString() == "org.mapstruct" && it.declaration.simpleName.asString() == "Mapper"
                    }
                }!!), Unit)
            }
        return invalidSymbols
    }

    @KotlinPoetKspPreview
    inner class MapperVisitor(val mapper: KSAnnotation) : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val packageName = classDeclaration.containingFile!!.packageName.asString()
            val className = "${classDeclaration.simpleName.asString()}Impl"

            val fileSpec = FileSpec.builder(
                packageName = packageName,
                fileName = classDeclaration.simpleName.asString()
            ).apply {
                val uses = getUses(mapper)
                addType(
                    TypeSpec.classBuilder(className)
                        .addSuperinterface(classDeclaration.toClassName())
                        .primaryConstructor(FunSpec.constructorBuilder().apply {
                            uses.forEach {
                                addParameter(
                                    useName(it),
                                    it.toClassName(),
                                )
                            }
                        }.build())
                        .addProperties(uses.map {
                            val propName = useName(it)
                            PropertySpec.builder(propName, it.toClassName()).initializer(propName).build()
                        }).apply {
                            classDeclaration.getAllFunctions().forEach { funDecl ->
                                if (funDecl.isAbstract) {
                                    funDecl.returnType?.resolve()?.let { rt ->
                                        val param = funDecl.parameters.first()

                                        logger.warn("Implementing mapper for map(${param.type.toTypeName()}) -> ${rt.toClassName()}")

                                        addFunction(
                                            FunSpec.builder(funDecl.simpleName.asString())
                                                .addModifiers(KModifier.OVERRIDE)
                                                .addParameter(param.name!!.asString(), param.type.toTypeName())
                                                .returns(rt.toClassName()).addCode(
                                                    mapUsingConstructor(param, rt, mapper, classDeclaration)
                                                )
                                                .build()
                                        )
                                    }
                                }
                            }
                        }.build()
                )
            }.build()

            codeGenerator.createNewFile(
                dependencies = Dependencies(aggregating = false),
                packageName = packageName,
                fileName = className
            ).use { outputStream ->
                outputStream.writer()
                    .use {
                        fileSpec.writeTo(it)
                    }
            }
        }

        private fun useName(it: KSType) = it.declaration.simpleName.getShortName()
            .replaceFirstChar { it.lowercase(Locale.getDefault()) }

        fun getTypeConverter(
            source: Pair<KSTypeReference, KSType>,
            target: Pair<KSTypeReference, KSType>,
            mapper: KSAnnotation,
            classDeclaration: KSClassDeclaration
        ): CodeBlock = buildCodeBlock {
            if (source.second == target.second) {
                add("it")
            } else {
                //Need to make sure that the annotations work
                val selfUse = classDeclaration.getAllFunctions().find { it.parameters.size == 1 && it.parameters[0].type.toTypeName() == source.first.toTypeName() && it.returnType?.toTypeName() == target.first.toTypeName() }
                val use = getUses(mapper).flatMap { u ->
                    (u.declaration as? KSClassDeclaration)?.getAllFunctions()?.mapNotNull {
                        it.takeIf { it.parameters.size == 1 && it.parameters[0].type.toTypeName() == source.first.toTypeName() && it.returnType?.toTypeName() == target.first.toTypeName() }
                            ?.let { u to it }
                    }?.toList() ?: emptyList()
                }.firstOrNull()

                val sourceDecl = source.second.declaration as KSClassDeclaration
                val targetDecl = target.second.declaration as KSClassDeclaration
                when {
                    selfUse != null -> addStatement("this.%L(it)", selfUse.simpleName.asString())
                    use != null -> addStatement("this.%L.%L(it)", useName(use.first), use.second.simpleName.asString())
                    sourceDecl.isCollection() && targetDecl.isList() ->
                        add("it.map { %L }", getTypeConverter(
                            source.first.element!!.typeArguments.first().type!!.let { it to it.resolve()},
                            target.first.element!!.typeArguments.first().type!!.let { it to it.resolve()},
                            mapper,
                            classDeclaration
                        ))
                    sourceDecl.isCollection() && targetDecl.isSet() ->
                        add("it.map { %L }.toSet()", getTypeConverter(
                            source.first.element!!.typeArguments.first().type!!.let { it to it.resolve()},
                            target.first.element!!.typeArguments.first().type!!.let { it to it.resolve()},
                            mapper,
                            classDeclaration
                        ))
                    sourceDecl.isMap() && targetDecl.isMap() ->
                        add("it.map { (k,v) -> k?.let { %L } to v?.let { %L } }.toMap()",
                            getTypeConverter(
                                source.first.element!!.typeArguments[0].type!!.let { it to it.resolve()},
                                target.first.element!!.typeArguments[0].type!!.let { it to it.resolve()},
                                mapper,
                                classDeclaration
                            ), getTypeConverter(
                                source.first.element!!.typeArguments[1].type!!.let { it to it.resolve()},
                                target.first.element!!.typeArguments[1].type!!.let { it to it.resolve()},
                                mapper,
                                classDeclaration
                            ))
                    sourceDecl.classKind == ClassKind.ENUM_CLASS && targetDecl.classKind == ClassKind.ENUM_CLASS ->
                        add("%T.valueOf(it.value)", targetDecl.toClassName())
                    else -> logger.error("No mapper was found for ${source.first.toTypeName()} -> ${target.first.toTypeName()}")
                }
            }
        }

        private fun getUses(mapper: KSAnnotation) =
            mapper.arguments.find { it.name!!.asString() == "uses" }?.let {
                (it.value as Collection<KSType>).toList()
            } ?: emptyList()

        fun mapUsingConstructor(
            param: KSValueParameter,
            target: KSType,
            mapper: KSAnnotation,
            classDeclaration: KSClassDeclaration
        ) = buildCodeBlock {
            val sourceClass = (param.type.resolve().declaration as? KSClassDeclaration)
                ?: throw IllegalStateException("Return type should be a Class")
            val targetClass = (target.declaration as? KSClassDeclaration)
                ?: throw IllegalStateException("Return type should be a Class")
            val parameters = targetClass.primaryConstructor!!.parameters

            val assignments = parameters.map { p ->
                val candidates = sourceClass.declarations.mapNotNull { it as? KSPropertyDeclaration }
                    .filter { it.simpleName.asString() == p.name?.asString() }.toList()
                p.name!!.asString() to (candidates.firstOrNull()?.let {
                    val cType = it.type.resolve()
                    val pType = p.type.resolve()
                    logger.warn("  -->  Mapping constructor parameter ${p.name!!.asString()}")
                    if (cType == pType) {
                        "${param.name?.asString()}.${it}"
                    } else {
                        val typeConverter: CodeBlock = getTypeConverter(it.type to cType, p.type to pType, mapper, classDeclaration)
                        "${param.name?.asString()}.${it}?.let { $typeConverter }"
                    }
                } ?: throw IllegalArgumentException("Cannot find a counterpart for ${p.name?.asString()}"))
            }

            add("return %T(%L)",
                target.toClassName(),
                buildCodeBlock {
                    assignments.forEach { (p, v) -> addStatement("$p = $v, ") }
                }
            )
        }
    }

}

private fun KSClassDeclaration.isCollection() = this.isList() || this.isSet()

private tailrec fun KSClassDeclaration.isList(): Boolean {
    val qn = this.qualifiedName?.asString()
    return if (qn == "java.util.List" || qn == "kotlin.collections.List") true else {
        val parentDecl = (parentDeclaration as? KSClassDeclaration)
        @Suppress("IfThenToElvis")
        if (parentDecl == null) false else parentDecl.isList()
    }
}

private tailrec fun KSClassDeclaration.isSet(): Boolean {
    val qn = this.qualifiedName?.asString()
    return if (qn == "java.util.Set" || qn == "kotlin.collections.Set") true else {
        val parentDecl = (parentDeclaration as? KSClassDeclaration)
        @Suppress("IfThenToElvis")
        if (parentDecl == null) false else parentDecl.isSet()
    }
}

private tailrec fun KSClassDeclaration.isMap(): Boolean {
    val qn = this.qualifiedName?.asString()
    return if (qn == "java.util.Map" || qn == "kotlin.collections.Map") true else {
        val parentDecl = (parentDeclaration as? KSClassDeclaration)
        @Suppress("IfThenToElvis")
        if (parentDecl == null) false else parentDecl.isMap()
    }
}

@KotlinPoetKspPreview
@KspExperimental
class MapperProcessorProvider : SymbolProcessorProvider {
    override fun create(
        env: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return MapperProcessor(env.codeGenerator, env.logger)
    }
}

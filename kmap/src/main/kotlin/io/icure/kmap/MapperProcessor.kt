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
import com.squareup.kotlinpoet.ClassName
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
import io.icure.kmap.exception.ShouldDeferException
import io.icure.kmap.option.Mapping
import java.io.OutputStream
import java.util.*

fun OutputStream.appendText(str: String) {
    this.write(str.toByteArray())
}

private fun KSClassDeclaration.mapperAnnotation() = annotations.find {
    it.annotationType.resolve().let {
        it.declaration.packageName.asString() == "org.mapstruct" && it.declaration.simpleName.asString() == "Mapper"
    }
}!!

private fun KSAnnotation.mapperUses() =
    arguments.find { it.name!!.asString() == "uses" }?.let {
        (it.value as Collection<KSType>).toList()
    } ?: emptyList()

private fun KSAnnotation.mapperComponentModel() =
    arguments.find { it.name!!.asString() == "componentModel" }?.let {
        it.value?.toString()
    }

private fun KSAnnotation.mappingsMappings() =
    arguments.find { it.name!!.asString() == "value" }?.let {
        (it.value as List<KSAnnotation>).toList().map {
            Mapping(
                target = it.arguments.find { it.name?.asString() == "target" }?.value as? String,
                source = it.arguments.find { it.name?.asString() == "source" }?.value as? String,
                ignore = it.arguments.find { it.name?.asString() == "ignore" }?.value as? Boolean ?: false,
                )
        }
    } ?: emptyList()


private fun KSClassDeclaration.isCollection() = this.isList() || this.isSet() || this.isSortedSet()

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

private tailrec fun KSClassDeclaration.isSortedSet(): Boolean {
    val qn = this.qualifiedName?.asString()
    return if (qn == "java.util.SortedSet") true else {
        val parentDecl = (parentDeclaration as? KSClassDeclaration)
        @Suppress("IfThenToElvis")
        if (parentDecl == null) false else parentDecl.isSortedSet()
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
class MapperProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger
) : SymbolProcessor {
    val typeConverters: Map<Pair<String, String>, CodeBlock> = mapOf()
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("org.mapstruct.Mapper")
        val invalidSymbols = symbols.filter { !it.validate() }.toList() +
                symbols
                    .mapNotNull { (it as? KSClassDeclaration)?.takeIf { it.validate() } }
                    .filter { cd ->
                        cd.classKind == ClassKind.INTERFACE || cd.classKind == ClassKind.CLASS && cd.modifiers.contains(
                            Modifier.ABSTRACT
                        )
                    }
                    .mapNotNull {
                        try {
                            it.accept(MapperVisitor(it.mapperAnnotation()), Unit); null
                        } catch (e: ShouldDeferException) {
                            logger.warn("Deferring processing of ${it.qualifiedName?.asString()}")
                            it
                        }
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
                val uses = mapper.mapperUses()
                addType(
                    TypeSpec.classBuilder(className).apply {
                        when (mapper.mapperComponentModel()) {
                            "spring" -> addAnnotation(ClassName("org.springframework.stereotype", "Service"))
                        }
                    }
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
                                        val mappings = funDecl.annotations.find {
                                            it.annotationType.resolve().let {
                                                it.declaration.packageName.asString() == "org.mapstruct" && it.declaration.simpleName.asString() == "Mappings"
                                            }
                                        }
                                        logger.info("Implementing mapper for map(${param.type.toTypeName()}) -> ${rt.toClassName()}")

                                        addFunction(
                                            FunSpec.builder(funDecl.simpleName.asString())
                                                .addModifiers(KModifier.OVERRIDE)
                                                .addParameter(param.name!!.asString(), param.type.toTypeName())
                                                .returns(rt.toClassName()).addCode(
                                                    mapUsingConstructor(param, rt, mapper, classDeclaration, mappings)
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
                val sourceTypeName = source.first.toTypeName(source.second)
                val targetTypeName = target.first.toTypeName(target.second)

                val selfUseFns = classDeclaration.getAllFunctions().filter { it.qualifiedName?.asString() != "equals" && it.parameters.size == 1 }
                val usesFns = mapper.mapperUses().flatMap { u -> (u.declaration as? KSClassDeclaration)?.getAllFunctions()
                    ?.filter { it.qualifiedName?.asString() != "equals" && it.parameters.size == 1 }?.map { u to it }?.toList() ?: emptyList() }

                val selfUse = selfUseFns.find {
                            it.parameters[0].validate() &&
                            it.returnType?.validate() == true &&
                            it.parameters[0].type.toTypeName() == sourceTypeName &&
                            it.returnType?.toTypeName() == targetTypeName
                }
                val use = usesFns.find { (_, fn) ->
                            fn.parameters[0].validate() &&
                            fn.returnType?.validate() == true &&
                            fn.parameters[0].type.toTypeName() == sourceTypeName &&
                            fn.returnType?.toTypeName() == targetTypeName
                }

                val sourceDecl = source.second.declaration as KSClassDeclaration
                val targetDecl = target.second.declaration as KSClassDeclaration
                when {
                    selfUse != null -> addStatement("this.%L(it)", selfUse.simpleName.asString())
                    use != null -> addStatement("this.%L.%L(it)", useName(use.first), use.second.simpleName.asString())
                    sourceDecl.isCollection() && targetDecl.isList() ->
                        add(
                            "it.map { %L }", getTypeConverter(
                                source.first.element!!.typeArguments.first().type!!.let { it to it.resolve() },
                                target.first.element!!.typeArguments.first().type!!.let { it to it.resolve() },
                                mapper,
                                classDeclaration
                            )
                        )
                    sourceDecl.isCollection() && targetDecl.isSet() ->
                        add(
                            "it.map { %L }.toSet()", getTypeConverter(
                                source.first.element!!.typeArguments.first().type!!.let { it to it.resolve() },
                                target.first.element!!.typeArguments.first().type!!.let { it to it.resolve() },
                                mapper,
                                classDeclaration
                            )
                        )
                    sourceDecl.isCollection() && targetDecl.isSortedSet() ->
                        add(
                            "it.map { %L }.toSortedSet()", getTypeConverter(
                                source.first.element!!.typeArguments.first().type!!.let { it to it.resolve() },
                                target.first.element!!.typeArguments.first().type!!.let { it to it.resolve() },
                                mapper,
                                classDeclaration
                            )
                        )
                    sourceDecl.isMap() && targetDecl.isMap() ->
                        add(
                            "it.map { (k,v) -> Pair(k?.let { %L }, v?.let { %L }) }.toMap()",
                            getTypeConverter(
                                source.first.element!!.typeArguments[0].type!!.let { it to it.resolve() },
                                target.first.element!!.typeArguments[0].type!!.let { it to it.resolve() },
                                mapper,
                                classDeclaration
                            ), getTypeConverter(
                                source.first.element!!.typeArguments[1].type!!.let { it to it.resolve() },
                                target.first.element!!.typeArguments[1].type!!.let { it to it.resolve() },
                                mapper,
                                classDeclaration
                            )
                        )
                    sourceDecl.classKind == ClassKind.ENUM_CLASS && targetDecl.classKind == ClassKind.ENUM_CLASS ->
                        add("%T.valueOf(it.name)", targetDecl.toClassName())
                    source.second.isMarkedNullable && target.second.isMarkedNullable -> {
                        add("it?.let { %L }", getTypeConverter(
                            source.copy(second = source.second.makeNotNullable()), target.copy(second = target.second.makeNotNullable()), mapper, classDeclaration
                        ))
                    }
                    target.second.isMarkedNullable -> {
                        add("%L", getTypeConverter(
                            source, target.copy(second = target.second.makeNotNullable()), mapper, classDeclaration
                        ))
                    }
                    else -> {
                        val mapperClassName = classDeclaration.toClassName()
                        val missingSelfUseFns = selfUseFns.filter { !it.validate() || it.returnType?.validate() == false }.map { "this" to it.qualifiedName?.asString() }.toList()
                        val missingUsesFns = usesFns.filter { (_, fn) -> !fn.validate() || fn.returnType?.validate() == false }.map { (u, fn) -> u.toTypeName().toString() to fn.qualifiedName?.asString() }.toList()
                        if (missingSelfUseFns.isNotEmpty() || missingUsesFns.isNotEmpty()) {
                            logger.warn("No mapper was found for $sourceTypeName -> $targetTypeName in class $mapperClassName during round, those mappers cannot be validated: ${
                                (missingSelfUseFns + missingUsesFns).joinToString(",") { (a, b) -> "$a.$b" }
                            } do not validate")
                            throw ShouldDeferException()
                        } else {
                            logger.error("No mapper was found for $sourceTypeName -> $targetTypeName in class $mapperClassName")
                        }
                    }
                }
            }
        }

        fun mapUsingConstructor(
            source: KSValueParameter,
            target: KSType,
            mapper: KSAnnotation,
            classDeclaration: KSClassDeclaration,
            mappings: KSAnnotation?
        ) = buildCodeBlock {
            val sourceClass = (source.type.resolve().declaration as? KSClassDeclaration)
                ?: throw IllegalStateException("Return type should be a Class")
            val targetClass = (target.declaration as? KSClassDeclaration)
                ?: throw IllegalStateException("Return type should be a Class")
            val parameters = targetClass.primaryConstructor!!.parameters

            val assignments = parameters.mapNotNull { p ->
                if (mappings?.mappingsMappings()?.any { it.target == p.name?.asString() && it.ignore } == true) {
                    null
                } else {
                    val sourceName = p.name?.asString()?.let { t ->
                        mappings?.mappingsMappings()?.find { it.target == t && it.source != null }?.source ?: t
                    }
                    val candidates = sourceClass.declarations.mapNotNull { it as? KSPropertyDeclaration }
                        .filter { it.simpleName.asString() == sourceName }.toList()

                    candidates.firstOrNull()?.let {
                        val cType = it.type.resolve()
                        val pType = p.type.resolve()

                        if (!it.type.validate()) {
                            logger.warn("Deferring parameter, cannot resolve ${it.qualifiedName?.asString()}")
                            throw ShouldDeferException()
                        }
                        if (!p.type.validate()) {
                            logger.warn("Deferring parameter, cannot resolve ${p.name?.asString()}")
                            throw ShouldDeferException()
                        }

                        val cTypeName = it.type.toTypeName(cType)
                        val pTypeName = p.type.toTypeName(pType)

                        if (cTypeName == pTypeName) {
                            "${source.name?.asString()}.${it}"
                        } else {
                            val typeConverter: CodeBlock =
                                getTypeConverter(it.type to cType, p.type to pType, mapper, classDeclaration)
                            "${source.name?.asString()}.${it}?.let { $typeConverter }"
                        }
                    }?.let { p.name!!.asString() to it }
                        ?: null.also { logger.error("Cannot find a counterpart for ${p.name?.asString()} when mapping from ${sourceClass.toClassName()} to ${targetClass.toClassName()}") }
                }
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

@KotlinPoetKspPreview
@KspExperimental
class MapperProcessorProvider : SymbolProcessorProvider {
    override fun create(
        env: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return MapperProcessor(env.codeGenerator, env.logger)
    }
}

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
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Nullability
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.isCollection
import com.squareup.kotlinpoet.ksp.isList
import com.squareup.kotlinpoet.ksp.isMap
import com.squareup.kotlinpoet.ksp.isMutableList
import com.squareup.kotlinpoet.ksp.isMutableMap
import com.squareup.kotlinpoet.ksp.isMutableSet
import com.squareup.kotlinpoet.ksp.isSet
import com.squareup.kotlinpoet.ksp.isSortedSet
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toResolvedTypeName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import io.icure.kmap.exception.ShouldDeferException
import io.icure.kmap.option.Mapping
import java.io.OutputStream
import java.util.*

fun OutputStream.appendText(str: String) {
    this.write(str.toByteArray())
}

private fun KSClassDeclaration.mapperAnnotation() = annotations.find {
    it.annotationType.resolve().let {
        (it.declaration.packageName.asString() == "org.mapstruct" || it.declaration.packageName.asString() == "io.icure.kmap") &&
                it.declaration.simpleName.asString() == "Mapper"
    }
}!!

private fun KSAnnotation.mapperUses() =
    arguments.find { it.name!!.asString() == "uses" }?.let {
        @Suppress("UNCHECKED_CAST")
        (it.value as Collection<KSType>).toList()
    } ?: emptyList()

private fun KSAnnotation.mapperComponentModel() =
    arguments.find { it.name!!.asString() == "componentModel" }?.let {
        it.value?.toString()
    }

private fun KSAnnotation.mappingsMappings() =
    arguments.find { it.name!!.asString() == "value" }?.let {
        @Suppress("UNCHECKED_CAST")
        (it.value as List<KSAnnotation>).toList().map {
            Mapping(
                target = it.arguments.find { it.name?.asString() == "target" }?.value as? String,
                source = it.arguments.find { it.name?.asString() == "source" }?.value as? String,
                ignore = it.arguments.find { it.name?.asString() == "ignore" }?.value as? Boolean ?: false,
                expression = it.arguments.find { it.name?.asString() == "expression" }?.value as? String,
            )
        }
    } ?: emptyList()

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
            val annotationSpecs = classDeclaration.annotations.filterNot { it.shortName.asString().startsWith("Mapping") }.map { it.toAnnotationSpec() }.toList()
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
                        addAnnotations(annotationSpecs)
                    }.apply {
                        when(classDeclaration.classKind) {
                            ClassKind.INTERFACE -> addSuperinterface(classDeclaration.toClassName())
                            ClassKind.CLASS -> superclass(classDeclaration.toClassName())
                            else -> {
                                logger.error("Invalid class kind $className")
                                return
                            }
                        }
                    }.primaryConstructor(FunSpec.constructorBuilder().apply {
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
                                    funDecl.returnType?.let { rt ->
                                        val param = funDecl.parameters.first()
                                        val mappings = funDecl.annotations.find {
                                            it.annotationType.resolve().let {
                                                it.declaration.packageName.asString() == "org.mapstruct" && it.declaration.simpleName.asString() == "Mappings"
                                            }
                                        }
                                        logger.info("Implementing mapper for map(${param.type.toResolvedTypeName()}) -> ${rt.toResolvedTypeName()}")

                                        addFunction(
                                            FunSpec.builder(funDecl.simpleName.asString())
                                                .addModifiers(KModifier.OVERRIDE)
                                                .addTypeVariables(funDecl.typeParameters.map { it.toTypeVariableName() })
                                                .addParameter(param.name!!.asString(), param.type.toResolvedTypeName())
                                                .returns(rt.toResolvedTypeName()).addCode(
                                                    mapUsingPrimaryConstructor(
                                                        param,
                                                        rt.resolve(),
                                                        mapper,
                                                        classDeclaration,
                                                        mappings
                                                    )
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
            } else try {
                val sourceTypeName = source.first.toTypeName(source.second)
                val targetTypeName = target.first.toTypeName(target.second)

                val selfUseFns = classDeclaration.getAllFunctions()
                    .filter { it.qualifiedName?.asString() != "equals" && it.parameters.size == 1 }
                val usesFns = mapper.mapperUses().flatMap { u ->
                    (u.declaration as? KSClassDeclaration)?.getAllFunctions()
                        ?.filter { it.qualifiedName?.asString() != "equals" && it.parameters.size == 1 }
                        ?.map { u to it }?.toList() ?: emptyList()
                }

                val selfUse = selfUseFns.find {
                    isValidMappingFunction(it, sourceTypeName, targetTypeName)
                }
                val use = usesFns.find { (_, fn) ->
                    isValidMappingFunction(fn, sourceTypeName, targetTypeName)
                }

                val sourceDecl = source.second.declaration as? KSClassDeclaration
                val targetDecl = target.second.declaration as? KSClassDeclaration
                when {
                    sourceDecl == null || targetDecl == null -> add("it")
                    selfUse != null -> add("this.%L(it)", selfUse.simpleName.asString())
                    use != null -> add("this.%L.%L(it)", useName(use.first), use.second.simpleName.asString())
                    sourceDecl.isCollection() && targetDecl.isList() ->
                        add("it.map·{ %L }", getTypeArgumentConverter(0, source, target, mapper, classDeclaration))
                    sourceDecl.isCollection() && targetDecl.isMutableList() ->
                        add(
                            "it.map·{ %L }.toMutableList()",
                            getTypeArgumentConverter(0, source, target, mapper, classDeclaration)
                        )
                    sourceDecl.isCollection() && targetDecl.isSet() ->
                        add(
                            "it.map·{ %L }.toSet()",
                            getTypeArgumentConverter(0, source, target, mapper, classDeclaration)
                        )
                    sourceDecl.isCollection() && targetDecl.isMutableSet() ->
                        add(
                            "it.map·{ %L }.toMutableSet()",
                            getTypeArgumentConverter(0, source, target, mapper, classDeclaration)
                        )
                    sourceDecl.isCollection() && targetDecl.isSortedSet() ->
                        add(
                            "it.map·{ %L }.toSortedSet()",
                            getTypeArgumentConverter(0, source, target, mapper, classDeclaration)
                        )
                    (sourceDecl.isMap() || sourceDecl.isMutableMap()) && targetDecl.isMap() ->
                        add(
                            "it.map·{ (k,v) -> Pair(k?.let·{ %L }, v?.let·{ %L }) }.toMap()",
                            getTypeArgumentConverter(0, source, target, mapper, classDeclaration),
                            getTypeArgumentConverter(1, source, target, mapper, classDeclaration)
                        )
                    (sourceDecl.isMap() || sourceDecl.isMutableMap()) && targetDecl.isMutableMap() ->
                        add(
                            "it.map·{ (k,v) -> Pair(k?.let·{ %L }, v?.let·{ %L }) }.toMap().toMutableMap()",
                            getTypeArgumentConverter(0, source, target, mapper, classDeclaration),
                            getTypeArgumentConverter(1, source, target, mapper, classDeclaration)
                        )

                    sourceDecl.classKind == ClassKind.ENUM_CLASS && targetDecl.classKind == ClassKind.ENUM_CLASS ->
                        add("%T.valueOf(it.name)", targetDecl.toClassName())
                    source.second.isMarkedNullable && target.second.isMarkedNullable -> {
                        add(
                            "it?.let·{ %L }", getTypeConverter(
                                source.copy(second = source.second.makeNotNullable()),
                                target.copy(second = target.second.makeNotNullable()),
                                mapper,
                                classDeclaration
                            )
                        )
                    }
                    target.second.isMarkedNullable -> {
                        add(
                            "%L", getTypeConverter(
                                source, target.copy(second = target.second.makeNotNullable()), mapper, classDeclaration
                            )
                        )
                    }
                    else -> {
                        val mapperClassName = classDeclaration.toClassName()
                        val missingSelfUseFns =
                            selfUseFns.filter { !it.validate() || it.returnType?.validate() == false }
                                .map { "this" to it.qualifiedName?.asString() }.toList()
                        val missingUsesFns =
                            usesFns.filter { (_, fn) -> !fn.validate() || fn.returnType?.validate() == false }
                                .map { (u, fn) -> u.toTypeName().toString() to fn.qualifiedName?.asString() }.toList()
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
            } catch (e: Exception) {
                try {
                    val mapperClassName = classDeclaration.toClassName()
                    val sourceTypeName = source.first.toTypeName(source.second)
                    val targetTypeName = target.first.toTypeName(target.second)

                    logger.error("An error occurred while trying to get converter: $sourceTypeName -> $targetTypeName in class $mapperClassName : ${e}")
                } catch (e: Exception) {
                    logger.error("Internal error in getTypeConverter")
                }
            }
        }

        private fun getTypeArgumentConverter(
            idx: Int,
            source: Pair<KSTypeReference, KSType>,
            target: Pair<KSTypeReference, KSType>,
            mapper: KSAnnotation,
            classDeclaration: KSClassDeclaration
        ) = getTypeConverter(
            source.first.element!!.typeArguments[idx].type!!.let { it to it.resolve() },
            target.first.element!!.typeArguments[idx].type!!.let { it to it.resolve() },
            mapper,
            classDeclaration
        )

        private fun isValidMappingFunction(
            it: KSFunctionDeclaration,
            sourceTypeName: TypeName,
            targetTypeName: TypeName
        ) = it.parameters[0].validate() &&
                it.returnType?.validate() == true &&
                it.parameters[0].type.toResolvedTypeName() == sourceTypeName &&
                it.returnType?.toResolvedTypeName() == targetTypeName

        fun mapUsingPrimaryConstructor(
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
            val primaryConstructor = targetClass.primaryConstructor
            if (primaryConstructor == null) {
                logger.error("${targetClass.qualifiedName?.asString()} does not have a primary constructor.")
                return@buildCodeBlock
            }
            val assignments = primaryConstructor.parameters.mapNotNull { constructorParameter ->
                val mapping = mappings?.mappingsMappings()?.find { it.target == constructorParameter.name?.asString() }
                if (mapping != null && (mapping.ignore || mapping.expression?.isNotEmpty() == true)) {
                    when {
                        mapping.ignore -> null
                        mapping.expression != null -> {
                            val trimmed = mapping.expression.trim()
                            if (trimmed.startsWith("kotlin(")) {
                                mapping.target!! to trimmed.replace("""kotlin\((.+)\)""".toRegex(), "$1")
                            } else {
                                logger.error("Bad expression format for ${mapping.target} when mapping from ${sourceClass.toClassName()} to ${targetClass.toClassName()}")
                                null
                            }
                        }
                        else -> null
                    }
                } else {
                    val sourcePropertyName = constructorParameter.name?.asString()?.let { t ->
                        mappings?.mappingsMappings()?.find { it.target == t && it.source != null }?.source ?: t
                    }
                    val candidates = sourceClass.getAllProperties()
                        .filter { it.simpleName.asString() == sourcePropertyName }.toList()

                    val fallBackOnSourceParameterType =
                        source.type.takeIf { candidates.isEmpty() && sourcePropertyName == source.name?.asString() }

                    candidates.firstOrNull().let { property ->
                        val sourceType = property?.type ?: fallBackOnSourceParameterType
                        if (sourceType == null) {
                            null
                        } else {
                            val cType = sourceType.resolve()
                            val pType = constructorParameter.type.resolve()

                            if (!sourceType.validate()) {
                                logger.warn("Deferring parameter, cannot resolve ${property?.qualifiedName?.asString() ?: source.name?.asString()}")
                                throw ShouldDeferException()
                            }
                            if (!constructorParameter.type.validate()) {
                                logger.warn("Deferring parameter, cannot resolve ${constructorParameter.name?.asString()}")
                                throw ShouldDeferException()
                            }

                            val cTypeName = sourceType.toTypeName(cType)
                            val pTypeName = constructorParameter.type.toTypeName(pType)

                            val prefix = listOfNotNull(source.name?.asString(), property).joinToString(".")
                            val nullMarker = "".takeIf { cType.nullability == Nullability.NOT_NULL } ?: "?"

                            if (cTypeName == pTypeName) {
                                buildCodeBlock { add(prefix) }
                            } else {
                                val typeConverter: CodeBlock =
                                    getTypeConverter(
                                        sourceType to cType,
                                        constructorParameter.type to pType,
                                        mapper,
                                        classDeclaration
                                    )
                                buildCodeBlock {
                                    add("$prefix$nullMarker.let·{ %L }", typeConverter)
                                }
                            }
                        }
                    }?.let { constructorParameter.name!!.asString() to it }
                        ?: null.also { logger.error("Cannot find a counterpart for ${constructorParameter.name?.asString()} when mapping from ${sourceClass.toClassName()} to ${targetClass.toClassName()}") }
                }
            }

            add("return·%T(%L)",
                target.toClassName(),
                buildCodeBlock {
                    assignments.forEach { (p, v) -> addStatement("$p = %L, ", v) }
                }
            )
        }
    }
}

@KotlinPoetKspPreview
@KspExperimental
class MapperProcessorProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return MapperProcessor(environment.codeGenerator, environment.logger)
    }
}

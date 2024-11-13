package io.icure.kmap.mapping

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Nullability
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.buildCodeBlock
import com.squareup.kotlinpoet.ksp.isCollection
import com.squareup.kotlinpoet.ksp.isList
import com.squareup.kotlinpoet.ksp.isMap
import com.squareup.kotlinpoet.ksp.isMutableList
import com.squareup.kotlinpoet.ksp.isMutableMap
import com.squareup.kotlinpoet.ksp.isMutableSet
import com.squareup.kotlinpoet.ksp.isSet
import com.squareup.kotlinpoet.ksp.isSortedSet
import com.squareup.kotlinpoet.ksp.isString
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toResolvedTypeName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import io.icure.kmap.exception.ShouldDeferException
import java.util.*

class MapperVisitor(
	private val mapper: KSAnnotation,
	private val codeGenerator: CodeGenerator,
	private val logger: KSPLogger
) : KSVisitorVoid() {

	override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
		val packageName = classDeclaration.containingFile!!.packageName.asString()
		val className = "${classDeclaration.simpleName.asString()}Impl"
		val annotationSpecs =
			classDeclaration.annotations.filterNot { it.shortName.asString().startsWith("Mapper") }
				.map { it.toAnnotationSpec() }.toList()
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
					when (classDeclaration.classKind) {
						ClassKind.INTERFACE -> addSuperinterface(classDeclaration.toClassName())
						ClassKind.CLASS -> superclass(classDeclaration.toClassName())
						else -> {
							logger.error("Invalid class kind $className")
							return
						}
					}

					addAnnotation(
						AnnotationSpec.builder(ClassName("kotlin", "Suppress"))
							.addMember("%S", "RedundantVisibilityModifier").also {
								if (uses.isEmpty()) {
									it.addMember("%S", "RemoveEmptyPrimaryConstructor")
								}
							}.build()
					)
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
									val mappings = funDecl.annotations.find { annotation ->
										annotation.annotationType.resolve().let {
											it.declaration.packageName.asString() == "org.mapstruct" && it.declaration.simpleName.asString() == "Mappings"
										}
									}
									logger.info("Implementing mapper for map(${param.type.toResolvedTypeName()}) -> ${rt.toResolvedTypeName()}")

									addFunction(
										FunSpec.builder(funDecl.simpleName.asString())
											.addModifiers(KModifier.OVERRIDE)
											.addTypeVariables(funDecl.typeParameters.map { it.toTypeVariableName() })
											.addParameter(param.name!!.asString(), param.type.toResolvedTypeName())
											.also {
												if(param.type.hasDeprecatedProperties()) {
													it.addAnnotation(AnnotationSpec.builder(ClassName("kotlin", "Suppress")).addMember("%S", "DEPRECATION").build())
												}
											}
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

	private fun getTypeConverter(
		source: Pair<KSTypeReference, KSType>,
		target: Pair<KSTypeReference, KSType>,
		mapper: KSAnnotation,
		classDeclaration: KSClassDeclaration,
		nestLevel: Int = 0,
		paramPrefix: String = "x",
		doNotUseNestLevel: Boolean = false
	): CodeBlock = buildCodeBlock {
		val paramName = if (doNotUseNestLevel) {
			paramPrefix
		} else {
			"$paramPrefix$nestLevel"
		}
		if (source.second == target.second) {
			add(paramName)
		} else try {
			val sourceIsNullable = source.second.nullability == Nullability.NULLABLE
			val nullMarker = "".takeIf { !sourceIsNullable } ?: "?"

			// Mapping functions for specific types defined inside the mapper. Nullability counts because you may
			// want to define a mapping function for T? that does not consider T.
			val selfSourceTypeName = source.first.toTypeName(source.second)
			val selfTargetTypeName = target.first.toTypeName(target.second)
			val selfUseFns = classDeclaration.getAllFunctions()
				.filter { it.qualifiedName?.asString() != "equals" && it.parameters.size == 1 }
			val selfUse = selfUseFns.find {
				isValidMappingFunction(it, selfSourceTypeName, selfTargetTypeName)
			}

			// Mapping functions that come from other mappers passed through the constructor. Nullability does not
			// count, as mappers are usually T_ENTITY -> T_DTO

			val usesFns = mapper.mapperUses().flatMap { u ->
				(u.declaration as? KSClassDeclaration)?.getAllFunctions()
					?.filter { it.qualifiedName?.asString() != "equals" && it.parameters.size == 1 }
					?.map { u to it }?.toList() ?: emptyList()
			}
			// First I check if there is a mapper that goes from T_ENTITY -> T_DTO, if it does not exists, I check
			// T_ENTITY? -> T_DTO?
			// This is to avoid an additional level of indirection through a let
			val sourceTypeName = source.first.toTypeName(source.second.makeNotNullable())
			val targetTypeName = target.first.toTypeName(target.second.makeNotNullable())
			val use = usesFns.firstOrNull { (_, fn) ->
				isValidMappingFunction(fn, sourceTypeName, targetTypeName)
			}
			val sourceTypeNameNullable = source.first.toTypeName(source.second)
			val targetTypeNameNullable = target.first.toTypeName(target.second)
			val useNullable = usesFns.firstOrNull { (_, fn) ->
				isValidMappingFunction(fn, sourceTypeNameNullable, targetTypeNameNullable)
			}

			val sourceDecl = source.second.declaration as? KSClassDeclaration
			val targetDecl = target.second.declaration as? KSClassDeclaration
			when {
				sourceDecl == null || targetDecl == null -> add(paramName)
				selfUse != null -> add("this.%L($paramName)", selfUse.simpleName.asString())
				use != null && !sourceIsNullable -> add("this.%L.%L($paramName)", useName(use.first), use.second.simpleName.asString())
				useNullable != null -> add("this.%L.%L($paramName)", useName(useNullable.first), useNullable.second.simpleName.asString())
				sourceDecl.isCollection() && targetDecl.isList() ->
					add("$paramName$nullMarker.map·{ x${nestLevel+1} -> %L }", getTypeArgumentConverter(0, source, target, mapper, classDeclaration, nestLevel + 1, "x"))
				sourceDecl.isCollection() && targetDecl.isMutableList() ->
					add(
						"$paramName$nullMarker.map·{ x${nestLevel+1} -> %L }$nullMarker.toMutableList()",
						getTypeArgumentConverter(0, source, target, mapper, classDeclaration, nestLevel + 1, "x")
					)
				sourceDecl.isCollection() && targetDecl.isSet() ->
					add(
						"$paramName$nullMarker.map·{ x${nestLevel+1} -> %L }$nullMarker.toSet()",
						getTypeArgumentConverter(0, source, target, mapper, classDeclaration, nestLevel + 1, "x")
					)
				sourceDecl.isCollection() && targetDecl.isMutableSet() ->
					add(
						"$paramName$nullMarker.map·{ x${nestLevel+1} -> %L }$nullMarker.toMutableSet()",
						getTypeArgumentConverter(0, source, target, mapper, classDeclaration, nestLevel + 1, "x")
					)
				sourceDecl.isCollection() && targetDecl.isSortedSet() ->
					add(
						"$paramName$nullMarker.map·{ x${nestLevel+1} -> %L }$nullMarker.toSortedSet()",
						getTypeArgumentConverter(0, source, target, mapper, classDeclaration, nestLevel + 1, "x")
					)
				(sourceDecl.isMap() || sourceDecl.isMutableMap()) -> {
					val keyIsNullable = source.second.arguments[0].type?.resolve()?.isMarkedNullable != false
					val valueIsNullable = source.second.arguments[1].type?.resolve()?.isMarkedNullable != false
					val formatString = buildString {
						append("$paramName$nullMarker.map·{ (k$nestLevel, v$nestLevel) -> Pair(")

						if (keyIsNullable) {
							append("k$nestLevel?.let { kx${nestLevel+1} -> %L }")
						} else {
							append("%L")
						}
						append(", ")
						if (valueIsNullable) {
							append("v$nestLevel?.let { vx${nestLevel+1} -> %L }")
						} else {
							append("%L")
						}
						append(")}$nullMarker.toMap()")

						if (targetDecl.isMutableMap()) append("$nullMarker.toMutableMap()")
					}

					add(
						formatString,
						getTypeArgumentConverter(0, source, target, mapper, classDeclaration, if (keyIsNullable) nestLevel + 1 else nestLevel, if (keyIsNullable) "kx" else "k"),
						getTypeArgumentConverter(1, source, target, mapper, classDeclaration, if (keyIsNullable) nestLevel + 1 else nestLevel, if (keyIsNullable) "vx" else "v")
					)
				}
				sourceDecl.classKind == ClassKind.ENUM_CLASS && targetDecl.classKind == ClassKind.ENUM_CLASS && !sourceIsNullable->
					add("%T.valueOf($paramName.name)", targetDecl.toClassName())
				sourceDecl.classKind == ClassKind.ENUM_CLASS && targetDecl.isString() && !sourceIsNullable ->
					add("$paramName.name", targetDecl.toClassName())
				sourceDecl.isString() && targetDecl.classKind == ClassKind.ENUM_CLASS && !sourceIsNullable ->
					add("%L.valueOf($paramName)", targetDecl.toClassName())
				source.second.isMarkedNullable && target.second.isMarkedNullable -> {
					add(
						"$paramName?.let·{ x${nestLevel+1} -> %L }", getTypeConverter(
							source.copy(second = source.second.makeNotNullable()),
							target.copy(second = target.second.makeNotNullable()),
							mapper,
							classDeclaration,
							nestLevel + 1,
							"x"
						)
					)
				}
				target.second.isMarkedNullable -> {
					add(
						"%L",
						getTypeConverter(
							source, target.copy(second = target.second.makeNotNullable()), mapper, classDeclaration, nestLevel, paramPrefix
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

				logger.error("An error occurred while trying to get converter: $sourceTypeName -> $targetTypeName in class $mapperClassName : $e")
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
		classDeclaration: KSClassDeclaration,
		nestLevel: Int,
		paramPrefix: String
	) = getTypeConverter(
		source.first.element!!.typeArguments[idx].type!!.let { it to it.resolve() },
		target.first.element!!.typeArguments[idx].type!!.let { it to it.resolve() },
		mapper,
		classDeclaration,
		nestLevel,
		paramPrefix
	)

	private fun isValidMappingFunction(
		it: KSFunctionDeclaration,
		sourceTypeName: TypeName,
		targetTypeName: TypeName
	) = it.parameters[0].validate() &&
		it.returnType?.validate() == true &&
		it.parameters[0].type.toResolvedTypeName() == sourceTypeName &&
		it.returnType?.toResolvedTypeName() == targetTypeName

	private fun mapUsingPrimaryConstructor(
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

						when {
							cTypeName == pTypeName -> buildCodeBlock { add(prefix) }
							(cType.declaration as? KSClassDeclaration)?.let { it.isCollection() || it.isMap() || it.isMutableMap() } == true -> {
								val typeConverter = getTypeConverter(
									sourceType to cType,
									constructorParameter.type to pType,
									mapper,
									classDeclaration,
									0,
									prefix,
									doNotUseNestLevel = true
								)
								buildCodeBlock {
									add("%L", typeConverter)
								}
							}
							else -> {
								val typeConverter = getTypeConverter(
									sourceType to cType,
									constructorParameter.type to pType,
									mapper,
									classDeclaration
								)
								buildCodeBlock {
									add("$prefix$nullMarker.let·{ x0 -> %L }", typeConverter)
								}
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
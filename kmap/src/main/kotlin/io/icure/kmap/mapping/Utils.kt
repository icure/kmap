package io.icure.kmap.mapping

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import io.icure.kmap.Mapper
import io.icure.kmap.option.Mapping

/**
 * @return the [Mapper] annotations of a class.
 */
internal fun KSClassDeclaration.mapperAnnotation() = annotations.first {
	it.annotationType.resolve().let { annotation ->
		(annotation.declaration.packageName.asString() == "org.mapstruct" || annotation.declaration.packageName.asString() == "io.icure.kmap") &&
			annotation.declaration.simpleName.asString() == "Mapper"
	}
}

/**
 * @return a list of all the types corresponding to the other mapper classes that are needed to convert the current type
 * as defined in [Mapper.uses].
 */
internal fun KSAnnotation.mapperUses() =
	arguments.firstOrNull { it.name!!.asString() == "uses" }?.let {
		@Suppress("UNCHECKED_CAST")
		(it.value as Collection<KSType>).toList()
	} ?: emptyList()

internal fun KSAnnotation.mapperPassOnDefaults() =
	arguments.first { it.name!!.asString() == "defaultPassOnParameters" }.let { passOnDefaults ->
		fun getDefaultPassOnParameterDetails(passOnDefault: Any?): DefaultPassOnParameterDetails {
			passOnDefault as KSAnnotation
			val parameterName = passOnDefault.arguments.first { it.name!!.asString() == "parameterName" }.value as String
			val type = passOnDefault.arguments.first { it.name!!.asString() == "type" }.value as KSType
			val valueExpression = passOnDefault.arguments.first { it.name!!.asString() == "valueExpression" }.value as String
			val matchParameterName = passOnDefault.arguments.first { it.name!!.asString() == "matchParameterName" }.value as Boolean
			return DefaultPassOnParameterDetails(
				sourceName = parameterName,
				type = type,
				defaultValueExpression = valueExpression,
				matchName = matchParameterName,
			)
		}
		(passOnDefaults.value as? List<*>)?.map { getDefaultPassOnParameterDetails(it) }
			?: (passOnDefaults.value as Array<*>).map { getDefaultPassOnParameterDetails(it) }
	}

internal fun KSValueParameter.passOnAnnotation() = annotations.firstOrNull { it.shortName.getShortName() == "PassOnParameter" }

/**
 * @return the mapper component model, as defined in [Mapper.componentModel].
 */
internal fun KSAnnotation.mapperComponentModel() =
	arguments.find { it.name!!.asString() == "componentModel" }?.let {
		it.value?.toString()
	}

/**
 * @return a [List] of [Mapping] that define different rules to map specific fields.
 */
internal fun KSAnnotation.mappingsMappings() =
	arguments.find { it.name!!.asString() == "value" }?.let {
		@Suppress("UNCHECKED_CAST")
		(it.value as List<KSAnnotation>).toList().map { annotation ->
			Mapping(
				target = annotation.arguments.find { arg -> arg.name?.asString() == "target" }?.value as? String,
				source = annotation.arguments.find { arg -> arg.name?.asString() == "source" }?.value as? String,
				ignore = annotation.arguments.find { arg -> arg.name?.asString() == "ignore" }?.value as? Boolean ?: false,
				expression = annotation.arguments.find { arg -> arg.name?.asString() == "expression" }?.value as? String,
			)
		}
	} ?: emptyList()


internal fun KSTypeReference.hasDeprecatedProperties() =
	(this.resolve().declaration as? KSClassDeclaration)?.getAllProperties()?.toList()?.any {
		it.annotations.toList().firstOrNull { ann ->
			ann.shortName.asString().lowercase() == "deprecated"
		} != null
	} ?: false
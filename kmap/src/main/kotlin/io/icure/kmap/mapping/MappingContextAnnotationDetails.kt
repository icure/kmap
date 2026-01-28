package io.icure.kmap.mapping

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.Import

internal class MappingContextAnnotationDetails private constructor (
	/**
	 * Code to be added before entering a sub property mapping.
	 * Available placeholders:
	 * - %X: reference to this MappingContextCollector (may be nullable)
	 * - %P: reference or literal value of the String with the name of the property being entered
	 */
	private val beforeEnteringProperty: String?,
	/**
	 * Code to be added after exiting a sub property mapping.
	 * Available placeholders same as [beforeEnteringProperty].
	 */
	private val afterExitingProperty: String?,
	/**
	 * Code to be added before entering a list item mapping.
	 * Available placeholders:
	 * - %X: reference to this MappingContextCollector (may be nullable)
	 * - %I: reference or literal value of the index of the list item being entered
	 */
	private val beforeEnteringListItem: String?,
	/**
	 * Code to be added after exiting a list item mapping.
	 * Available placeholders same as [beforeEnteringListItem].
	 */
	private val afterExitingListItem: String?,
	/**
	 * Code to be added before entering a map entry mapping.
	 * Available placeholders:
	 * - %X: reference to this MappingContextCollector (may be nullable)
	 * - %K: reference or literal value of the key of the map entry being entered
	 */
	private val beforeEnteringMapEntry: String?,
	/**
	 * Code to be added after exiting a map entry mapping.
	 * Available placeholders same as [beforeEnteringMapEntry].
	 */
	private val afterExitingMapEntry: String?,
	/**
	 * Additional imports required for this MappingContextCollector to work.
	 */
	val additionalImports: List<String>
) {
	companion object {
		fun fromAnnotation(annotation: KSAnnotation): MappingContextAnnotationDetails {
			val additionalImports = annotation.arguments.first { it.name!!.asString() == "additionalImports" }.value.let {
				(it as? List<*>) ?: (it as Array<*>).toList()
			}.map { it as String }
			val beforeEnteringProperty = (annotation.arguments.first { it.name!!.asString() == "beforeEnteringProperty" }.value as String).takeIf { it.isNotBlank() }
			val afterExitingProperty = (annotation.arguments.first { it.name!!.asString() == "afterExitingProperty" }.value as String).takeIf { it.isNotBlank() }
			val beforeEnteringListItem = (annotation.arguments.first { it.name!!.asString() == "beforeEnteringListItem" }.value as String).takeIf { it.isNotBlank() }
			val afterExitingListItem = (annotation.arguments.first { it.name!!.asString() == "afterExitingListItem" }.value as String).takeIf { it.isNotBlank() }
			val beforeEnteringMapEntry = (annotation.arguments.first { it.name!!.asString() == "beforeEnteringMapEntry" }.value as String).takeIf { it.isNotBlank() }
			val afterExitingMapEntry = (annotation.arguments.first { it.name!!.asString() == "afterExitingMapEntry" }.value as String).takeIf { it.isNotBlank() }
			return MappingContextAnnotationDetails(
				beforeEnteringProperty = beforeEnteringProperty,
				afterExitingProperty = afterExitingProperty,
				beforeEnteringListItem = beforeEnteringListItem,
				afterExitingListItem = afterExitingListItem,
				beforeEnteringMapEntry = beforeEnteringMapEntry,
				afterExitingMapEntry = afterExitingMapEntry,
				additionalImports = additionalImports,
			)
		}
	}

	fun afterExitingProperty(
		contextCollectorRef: String,
		propertyNameRef: String
	): String? = afterExitingProperty?.replace("%X", contextCollectorRef)?.replace("%P", propertyNameRef)

	fun beforeEnteringProperty(
		contextCollectorRef: String,
		propertyNameRef: String
	): String? = beforeEnteringProperty?.replace("%X", contextCollectorRef)?.replace("%P", propertyNameRef)

	fun afterExitingListItem(
		contextCollectorRef: String,
		indexRef: String
	): String? = afterExitingListItem?.replace("%X", contextCollectorRef)?.replace("%I", indexRef)

	fun beforeEnteringListItem(
		contextCollectorRef: String,
		indexRef: String
	): String? = beforeEnteringListItem?.replace("%X", contextCollectorRef)?.replace("%I", indexRef)

	fun afterExitingMapEntry(
		contextCollectorRef: String,
		keyRef: String
	): String? = afterExitingMapEntry?.replace("%X", contextCollectorRef)?.replace("%K", keyRef)

	fun beforeEnteringMapEntry(
		contextCollectorRef: String,
		keyRef: String
	): String? = beforeEnteringMapEntry?.replace("%X", contextCollectorRef)?.replace("%K", keyRef)
}

internal class ContextCollectorDetails(
	val name: String,
	val type: KSType,
	val annotationDetails: MappingContextAnnotationDetails
)
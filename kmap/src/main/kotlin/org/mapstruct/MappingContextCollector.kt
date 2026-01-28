package org.mapstruct

/**
 * Mark a class that can be used to collect mapping context.
 * A mapping function can have at most one parameter of type MappingContextCollector.
 * A parameter that is of a type marked as MappingContextCollector can be automatically passed-on to other mapping
 * functions that use the same type as parameter.
 * It is also possible to pass default MappingContextCollectors in the same way as default pass on parameters.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class MappingContextCollector(
	/**
	 * Code to be added before entering a sub property mapping.
	 * Available placeholders:
	 * - %X: reference to this MappingContextCollector (may be nullable)
	 * - %P: reference or literal value of the String with the name of the property being entered
	 */
	val beforeEnteringProperty: String = "",
	/**
	 * Code to be added after exiting a sub property mapping.
	 * Available placeholders same as [beforeEnteringProperty].
	 */
	val afterExitingProperty: String = "",
	/**
	 * Code to be added before entering a list item mapping.
	 * Available placeholders:
	 * - %X: reference to this MappingContextCollector (may be nullable)
	 * - %I: reference or literal value of the index of the list item being entered
	 */
	val beforeEnteringListItem: String = "",
	/**
	 * Code to be added after exiting a list item mapping.
	 * Available placeholders same as [beforeEnteringListItem].
	 */
	val afterExitingListItem: String = "",
	/**
	 * Code to be added before entering a map entry mapping.
	 * Available placeholders:
	 * - %X: reference to this MappingContextCollector (may be nullable)
	 * - %K: reference or literal value of the key of the map entry being entered
	 */
	val beforeEnteringMapEntry: String = "",
	/**
	 * Code to be added after exiting a map entry mapping.
	 * Available placeholders same as [beforeEnteringMapEntry].
	 */
	val afterExitingMapEntry: String = "",
	/**
	 * Additional imports required for this MappingContextCollector to work.
	 */
	val additionalImports: Array<String> = []
)

package org.mapstruct

/**
 * Mark a parameter that should be given in input to a mapping method and can be passed on to other mapping methods
 * using PassOnParameters of the same type.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.BINARY)
annotation class PassOnParameter(
	/**
	 * If true indicates that this parameter can be passed-on only if both the target parameter type AND name match.
	 * If false (default), the type alone is used to determine if a parameter can be passed on.
	 */
	val matchParameterName: Boolean = false,
)

package org.mapstruct

import kotlin.reflect.KClass


@Target() // No targets, only used in other annotations
@Retention(AnnotationRetention.BINARY)
annotation class DefaultPassOnParameter(
	/**
	 * The type of the default parameter.
	 */
	val type: KClass<*>,
	/**
	 * A kotlin expression that provides the value for this default parameter.
	 */
	val valueExpression: String,
	/**
	 * The name of the generated parameter to use in the mapper methods.
	 */
	val parameterName: String,
	/**
	 * If true indicates that a parameter that is suitable for "pass-on" must match both in parameter name and type.
	 * If false (default), the type alone is used to determine if a parameter can be passed on.
	 */
	val matchParameterName: Boolean = false,
)
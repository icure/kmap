/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct

import org.mapstruct.control.MappingControl
import kotlin.reflect.KClass

/**
 * Configures the mapping of one bean attribute or enum constant.
 *
 *
 * The name of the mapped attribute or constant is to be specified via [.target]. For mapped bean attributes it
 * is assumed by default that the attribute has the same name in the source bean. Alternatively, one of
 * [.source], [.expression] or [.constant] can be specified to define the property source.
 *
 *
 *
 * In addition, the attributes [.dateFormat] and [.qualifiedBy] may be used to further define the
 * mapping.
 *
 *
 *
 *
 * **Example 1:** Implicitly mapping fields with the same name:
 *
 * <pre>`
 * // Both classes HumanDto and Human have property with name "fullName"
 * // properties with the same name will be mapped implicitly
 * &#64;Mapper
 * public interface HumanMapper {
 * HumanDto toHumanDto(Human human)
 * }
`</pre> *
 * <pre>`
 * // generates:
 * &#64;Override
 * public HumanDto toHumanDto(Human human) {
 * humanDto.setFullName( human.getFullName() );
 * // ...
 * }
`</pre> *
 *
 *
 * **Example 2:** Mapping properties with different names
 * <pre>`
 * // We need map Human.companyName to HumanDto.company
 * // we can use &#64;Mapping with parameters [.source] and [.source]
 * &#64;Mapper
 * public interface HumanMapper {
 * &#64;Mapping(source="companyName", target="company")
 * HumanDto toHumanDto(Human human)
 * }
`</pre> *
 * <pre>`
 * // generates:
 * &#64;Override
 * public HumanDto toHumanDto(Human human) {
 * humanDto.setCompany( human.getCompanyName() );
 * // ...
 * }
`</pre> *
 *
 *
 * **Example 3:** Mapping with expression
 * **IMPORTANT NOTE:** Now it works only for Java
 *
 * <pre>`
 * // We need map Human.name to HumanDto.countNameSymbols.
 * // we can use [.expression] for it
 * &#64;Mapper
 * public interface HumanMapper {
 * &#64;Mapping(target="countNameSymbols", expression="java(human.getName().length())")
 * HumanDto toHumanDto(Human human)
 * }
`</pre> *
 * <pre>`
 * // generates:
 * &#64;Override
 * public HumanDto toHumanDto(Human human) {
 * humanDto.setCountNameSymbols( human.getName().length() );
 * //...
 * }
`</pre> *
 *
 *
 * **Example 4:** Mapping to constant
 *
 * <pre>`
 * // We need map HumanDto.name to string constant "Unknown"
 * // we can use [.constant] for it
 * &#64;Mapper
 * public interface HumanMapper {
 * &#64;Mapping(target="name", constant="Unknown")
 * HumanDto toHumanDto(Human human)
 * }
`</pre> *
 * <pre>`
 * // generates
 * &#64;Override
 * public HumanDto toHumanDto(Human human) {
 * humanDto.setName( "Unknown" );
 * // ...
 * }
`</pre> *
 *
 *
 * **Example 5:** Mapping with default value
 *
 * <pre>`
 * // We need map Human.name to HumanDto.fullName, but if Human.name == null, then set value "Somebody"
 * // we can use [.defaultValue] or [.defaultExpression] for it
 * &#64;Mapper
 * public interface HumanMapper {
 * &#64;Mapping(source="name", target="name", defaultValue="Somebody")
 * HumanDto toHumanDto(Human human)
 * }
`</pre> *
 * <pre>`
 * // generates
 * &#64;Override
 * public HumanDto toHumanDto(Human human) {
 * if ( human.getName() != null ) {
 * humanDto.setFullName( human.getName() );
 * }
 * else {
 * humanDto.setFullName( "Somebody" );
 * }
 * // ...
 * }
`</pre> *
 *
 * **IMPORTANT NOTE:** the enum mapping capability is deprecated and replaced by [ValueMapping] it
 * will be removed in subsequent versions.
 *
 * @author Gunnar Morling
 */
@JvmRepeatable(Mappings::class)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.ANNOTATION_CLASS
)
annotation class Mapping(
    /**
     * The target name of the configured property as defined by the JavaBeans specification. The same target property
     * must not be mapped more than once.
     *
     *
     * If used to map an enum constant, the name of the constant member is to be given. In this case, several values
     * from the source enum may be mapped to the same value of the target enum.
     *
     * @return The target name of the configured property or enum constant
     */
    val target: String,
    /**
     * The source to use for this mapping. This can either be:
     *
     *  1. The source name of the configured property as defined by the JavaBeans specification.
     *
     *
     * This may either be a simple property name (e.g. "address") or a dot-separated property path (e.g. "address.city"
     * or "address.city.name"). In case the annotated method has several source parameters, the property name must
     * qualified with the parameter name, e.g. "addressParam.city".
     *  1. When no matching property is found, MapStruct looks for a matching parameter name instead.
     *  1. When used to map an enum constant, the name of the constant member is to be given.
     *
     * This attribute can not be used together with [.constant] or [.expression].
     *
     * @return The source name of the configured property or enum constant.
     */
    val source: String = "",
    /**
     * A format string as processable by [SimpleDateFormat] if the attribute is mapped from `String` to
     * [Date] or vice-versa. Will be ignored for all other attribute types and when mapping enum constants.
     *
     * @return A date format string as processable by [SimpleDateFormat].
     */
    val dateFormat: String = "",
    /**
     * A format string as processable by [DecimalFormat] if the annotated method maps from a
     * [Number] to a [String] or vice-versa. Will be ignored for all other element types.
     *
     * @return A decimal format string as processable by [DecimalFormat].
     */
    val numberFormat: String = "",
    /**
     * A constant [String] based on which the specified target property is to be set.
     *
     *
     * When the designated target property is of type:
     *
     *
     *  1. primitive or boxed (e.g. `java.lang.Long`).
     *
     *
     * MapStruct checks whether the primitive can be assigned as valid literal to the primitive or boxed type.
     *
     *
     *  *
     * If possible, MapStruct assigns as literal.
     *
     *  *
     * If not possible, MapStruct will try to apply a user defined mapping method.
     *
     *
     *
     *  1. other
     *
     *
     * MapStruct handles the constant as `String`. The value will be converted by applying a matching method,
     * type conversion method or built-in conversion.
     *
     *
     *
     *
     *
     *
     * This attribute can not be used together with [.source], [.defaultValue],
     * [.defaultExpression] or [.expression].
     *
     * @return A constant `String` constant specifying the value for the designated target property
     */
    val constant: String = "",
    /**
     * An expression [String] based on which the specified target property is to be set.
     *
     *
     * Currently, Java is the only supported "expression language" and expressions must be given in form of Java
     * expressions using the following format: `java(<EXPRESSION>)`. For instance the mapping:
     * <pre>`
     * &#64;Mapping(
     * target = "someProp",
     * expression = "java(new TimeAndFormat( s.getTime(), s.getFormat() ))"
     * )
    `</pre> *
     *
     *
     * will cause the following target property assignment to be generated:
     *
     *
     * `targetBean.setSomeProp( new TimeAndFormat( s.getTime(), s.getFormat() ) )`.
     *
     *
     * Any types referenced in expressions must be given via their fully-qualified name. Alternatively, types can be
     * imported via [Mapper.imports].
     *
     *
     * This attribute can not be used together with [.source], [.defaultValue],
     * [.defaultExpression], [.qualifiedBy], [.qualifiedByName] or [.constant].
     *
     * @return An expression specifying the value for the designated target property
     */
    val expression: String = "",
    /**
     * A defaultExpression [String] based on which the specified target property is to be set
     * if and only if the specified source property is null.
     *
     *
     * Currently, Java is the only supported "expression language" and expressions must be given in form of Java
     * expressions using the following format: `java(<EXPRESSION>)`. For instance the mapping:
     * <pre>`
     * &#64;Mapping(
     * target = "someProp",
     * defaultExpression = "java(new TimeAndFormat( s.getTime(), s.getFormat() ))"
     * )
    `</pre> *
     *
     *
     * will cause the following target property assignment to be generated:
     *
     *
     * `targetBean.setSomeProp( new TimeAndFormat( s.getTime(), s.getFormat() ) )`.
     *
     *
     * Any types referenced in expressions must be given via their fully-qualified name. Alternatively, types can be
     * imported via [Mapper.imports].
     *
     *
     * This attribute can not be used together with [.expression], [.defaultValue]
     * or [.constant].
     *
     * @return An expression specifying a defaultValue for the designated target property if the designated source
     * property is null
     *
     * @since 1.3
     */
    val defaultExpression: String = "",
    /**
     * Whether the property specified via [.target] should be ignored by the generated mapping method or not.
     * This can be useful when certain attributes should not be propagated from source or target or when properties in
     * the target object are populated using a decorator and thus would be reported as unmapped target property by
     * default.
     *
     * @return `true` if the given property should be ignored, `false` otherwise
     */
    val ignore: Boolean = false,
    /**
     * A qualifier can be specified to aid the selection process of a suitable mapper. This is useful in case multiple
     * mapping methods (hand written or generated) qualify and thus would result in an 'Ambiguous mapping methods found'
     * error. A qualifier is a custom annotation and can be placed on a hand written mapper class or a method.
     *
     * @return the qualifiers
     * @see Qualifier
     */
    val qualifiedBy: Array<KClass<out Annotation>> = [],
    /**
     * String-based form of qualifiers; When looking for a suitable mapping method for a given property, MapStruct will
     * only consider those methods carrying directly or indirectly (i.e. on the class-level) a [Named] annotation
     * for each of the specified qualifier names.
     *
     *
     * Note that annotation-based qualifiers are generally preferable as they allow more easily to find references and
     * are safe for refactorings, but name-based qualifiers can be a less verbose alternative when requiring a large
     * number of qualifiers as no custom annotation types are needed.
     *
     * @return One or more qualifier name(s)
     * @see .qualifiedBy
     * @see Named
     */
    val qualifiedByName: Array<String> = [],
    /**
     * Specifies the result type of the mapping method to be used in case multiple mapping methods qualify.
     *
     * @return the resultType to select
     */
    val resultType: KClass<*> = Unit::class,
    /**
     * One or more properties of the result type on which the mapped property depends. The generated method
     * implementation will invoke the setters of the result type ordered so that the given dependency relationship(s)
     * are satisfied. Useful in case one property setter depends on the state of another property of the result type.
     *
     *
     * An error will be raised in case a cycle in the dependency relationships is detected.
     *
     * @return the dependencies of the mapped property
     */
    val dependsOn: Array<String> = [],
    /**
     * In case the source property is `null`, the provided default [String] value is set.
     *
     *
     * When the designated target property is of type:
     *
     *
     *  1. primitive or boxed (e.g. `java.lang.Long`).
     *
     *
     * MapStruct checks whether the primitive can be assigned as valid literal to the primitive or boxed type.
     *
     *
     *  *
     * If possible, MapStruct assigns as literal.
     *
     *  *
     * If not possible, MapStruct will try to apply a user defined mapping method.
     *
     *
     *
     *
     *
     *  1. other
     *
     *
     * MapStruct handles the constant as `String`. The value will be converted by applying a matching method,
     * type conversion method or built-in conversion.
     *
     *
     *
     *
     *
     *
     * This attribute can not be used together with [.constant], [.expression]
     * or [.defaultExpression].
     *
     * @return Default value to set in case the source property is `null`.
     */
    val defaultValue: String = "",
    /**
     * Determines when to include a null check on the source property value of a bean mapping.
     *
     * Can be overridden by the one on [MapperConfig], [Mapper] or [BeanMapping].
     *
     * @since 1.3
     *
     * @return strategy how to do null checking
     */
    val nullValueCheckStrategy: NullValueCheckStrategy = NullValueCheckStrategy.ON_IMPLICIT_CONVERSION,
    /**
     * The strategy to be applied when the source property is `null` or not present. If no strategy is configured,
     * the strategy given via [MapperConfig.nullValuePropertyMappingStrategy],
     * [BeanMapping.nullValuePropertyMappingStrategy] or
     * [Mapper.nullValuePropertyMappingStrategy] will be applied.
     *
     * [NullValuePropertyMappingStrategy.SET_TO_NULL] will be used by default.
     *
     * @since 1.3
     *
     * @return The strategy to be applied when `null` is passed as source property value or the source property
     * is not present.
     */
    val nullValuePropertyMappingStrategy: NullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
    /**
     * Allows detailed control over the mapping process.
     *
     * @return the mapping control
     *
     * @since 1.4
     *
     * @see org.mapstruct.control.DeepClone
     *
     * @see org.mapstruct.control.NoComplexMapping
     *
     * @see MappingControl
     */
    val mappingControl: KClass<out Annotation> = MappingControl::class
)

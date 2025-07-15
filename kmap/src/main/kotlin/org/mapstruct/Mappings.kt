/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct

/**
 * Configures the mappings of several bean attributes.
 *
 *
 * **TIP: When using Java 8 or later, you can omit the @Mappings
 * wrapper annotation and directly specify several @Mapping annotations on one method.**
 *
 *
 * These two examples are equal.
 *
 * <pre>`
 * // before Java 8
 * &#64;Mapper
 * public interface MyMapper {
 * &#64;Mappings({
 * &#64;Mapping(source = "first", target = "firstProperty"),
 * &#64;Mapping(source = "second", target = "secondProperty")
 * })
 * HumanDto toHumanDto(Human human);
 * }
`</pre> *
 * <pre>`
 * // Java 8 and later
 * &#64;Mapper
 * public interface MyMapper {
 * &#64;Mapping(source = "first", target = "firstProperty"),
 * &#64;Mapping(source = "second", target = "secondProperty")
 * HumanDto toHumanDto(Human human);
 * }
`</pre> *
 *
 * @author Gunnar Morling
 */
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.ANNOTATION_CLASS
)
annotation class Mappings(
    /**
     * The configuration of the bean attributes.
     *
     * @return The configuration of the bean attributes.
     */
    vararg val value: Mapping
)

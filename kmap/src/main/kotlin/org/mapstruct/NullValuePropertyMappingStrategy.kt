package org.mapstruct

/**
 * Strategy for dealing with `null` or not present properties in the source bean. The
 * [NullValuePropertyMappingStrategy] can be defined on [MapperConfig], [Mapper], [BeanMapping]
 * and [Mapping].
 * Precedence is arranged  in the reverse order. So [Mapping] will override [BeanMapping], will
 * overide [Mapper]
 *
 * The enum **only applies to update methods**: methods that update a pre-existing target (annotated with
 * `@`[MappingTarget]).
 *
 *
 *
 * **Note**: some types of mappings (collections, maps), in which MapStruct is instructed to use a getter or adder
 * as target accessor see [CollectionMappingStrategy], MapStruct will always generate a source property
 * null check, regardless the value of the [NullValuePropertyMappingStrategy] to avoid addition of `null`
 * to the target collection or map. Since the target is assumed to be initialised this strategy will not be applied.
 *
 * @author Sjaak Derksen
 * @since 1.3
 */
enum class NullValuePropertyMappingStrategy {
    /**
     * If a source bean property equals `null` the target bean property will be set explicitly to `null`.
     */
    SET_TO_NULL,

    /**
     * If a source bean property equals `null` the target bean property will be set to its default value.
     *
     *
     * This means:
     *
     *  1. For `List` MapStruct generates an `ArrayList`
     *  1. For `Map` a `HashMap`
     *  1. For arrays an empty array
     *  1. For `String` `""`
     *  1. for primitive / boxed types a representation of `0` or `false`
     *  1. For all other objects an new instance is created, requiring an empty constructor.
     *
     *
     *
     * Make sure that a [Mapping.defaultValue] is defined if no empty constructor is available on
     * the default value.
     */
    SET_TO_DEFAULT,

    /**
     * If a source bean property equals `null` the target bean property will be ignored and retain its
     * existing value.
     */
    IGNORE
}

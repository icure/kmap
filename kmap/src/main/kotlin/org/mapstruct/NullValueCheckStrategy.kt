package org.mapstruct

/**
 * Strategy for dealing with null source values.
 *
 * **Note:** This strategy is not in effect when the a specific source presence check method is defined
 * in the service provider interface (SPI).
 *
 *
 * **Note**: some types of mappings (collections, maps), in which MapStruct is instructed to use a getter or adder
 * as target accessor see [CollectionMappingStrategy], MapStruct will always generate a source property null
 * check, regardless the value of the [NullValueCheckStrategy] to avoid addition of `null` to the target
 * collection or map.
 *
 * @author Sean Huang
 */
enum class NullValueCheckStrategy {
    /**
     * This option includes a null check. When:
     * <br></br>
     * <br></br>
     *
     *  1. a source value is directly assigned to a target
     *  1. a source value assigned to a target by calling a type conversion on the target first
     *
     * <br></br>
     * **NOTE:** mapping methods (generated or hand written) are excluded from this null check. They are intended to
     * handle a null source value as 'valid' input.
     *
     */
    ON_IMPLICIT_CONVERSION,

    /**
     * This option always includes a null check.
     */
    ALWAYS
}

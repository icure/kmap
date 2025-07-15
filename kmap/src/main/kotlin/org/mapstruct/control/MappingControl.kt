/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.control

/**
 * Controls which means of mapping are considered between the source and the target in mappings.
 *
 *
 *
 * There are several applications of `MappingControl` conceivable. One application, "deep cloning" is
 * explained below in the example.
 *
 *
 *
 *
 * Another application is controlling so called "complex mappings", which are not always desirable and sometimes lead to
 * unexpected behaviour and prolonged compilation time.
 *
 *
 *
 * **Example:**Cloning of an object
 *
 *
 * When all methods are allowed, MapStruct would make a shallow copy. It would take the `ShelveDTO` in
 * the `FridgeDTO` and directly enter that as target on the target `FridgeDTO`. By disabling all
 * other kinds of mappings apart from [Use.MAPPING_METHOD], see [DeepClone] MapStruct is
 * forced to generate mapping methods all through the object graph `FridgeDTO` and hence create a deep clone.
 *
 * <pre>`
 * public class FridgeDTO {
 *
 * private ShelveDTO shelve;
 *
 * public ShelveDTO getShelve() {
 * return shelve;
 * }
 *
 * public void setShelve(ShelveDTO shelve) {
 * this.shelve = shelve;
 * }
 * }
`</pre> *
 * <pre>`
 * public class ShelveDTO {
 *
 * private CoolBeerDTO coolBeer;
 *
 * public CoolBeerDTO getCoolBeer() {
 * return coolBeer;
 * }
 *
 * public void setCoolBeer(CoolBeerDTO coolBeer) {
 * this.coolBeer = coolBeer;
 * }
 * }
`</pre> *
 * <pre>`
 * public class CoolBeerDTO {
 *
 * private String beerCount;
 *
 * public String getBeerCount() {
 * return beerCount;
 * }
 *
 * public void setBeerCount(String beerCount) {
 * this.beerCount = beerCount;
 * }
 * }
`</pre> *
 *
 * <pre>`
 * &#64;Mapper(mappingControl = DeepClone.class)
 * public interface CloningMapper {
 *
 * CloningMapper INSTANCE = Mappers.getMapper( CloningMapper.class );
 *
 * FridgeDTO clone(FridgeDTO in);
 *
 * }
`</pre> *
 *
 * @author Sjaak Derksen
 *
 * @since 1.4
 */
@Retention(AnnotationRetention.BINARY)
@JvmRepeatable(MappingControls::class)
@Target(AnnotationTarget.ANNOTATION_CLASS)
@MappingControl(
    MappingControl.Use.DIRECT
)
@MappingControl(MappingControl.Use.BUILT_IN_CONVERSION)
@MappingControl(MappingControl.Use.MAPPING_METHOD)
@MappingControl(
    MappingControl.Use.COMPLEX_MAPPING
)
annotation class MappingControl(val value: Use) {
    enum class Use {
        /**
         * Controls the mapping, allows for type conversion from source type to target type
         *
         *
         * Type conversions are typically supported directly in Java. The "toString()" is such an example,
         * which allows for mapping for instance a  [Number] type to a [String].
         *
         *
         * Please refer to the MapStruct guide for more info.
         *
         * @since 1.4
         */
        BUILT_IN_CONVERSION,

        /**
         * Controls the mapping from source to target type, allows mapping by calling:
         *
         *  1. A type conversion, passed into a mapping method
         *  1. A mapping method, passed into a type conversion
         *  1. A mapping method passed into another mapping method
         *
         *
         * @since 1.4
         */
        COMPLEX_MAPPING,

        /**
         * Controls the mapping, allows for a direct mapping from source type to target type.
         *
         *
         * This means if source type and target type are of the same type, MapStruct will not perform
         * any mappings anymore and assign the target to the source direct.
         *
         *
         * An exception are types from the package [java], which will be mapped always directly.
         *
         * @since 1.4
         */
        DIRECT,

        /**
         * Controls the mapping, allows for Direct Mapping from source type to target type.
         *
         *
         * The mapping method can be either a custom referred mapping method, or a MapStruct built in
         * mapping method.
         *
         * @since 1.4
         */
        MAPPING_METHOD
    }
}

package org.mapstruct


/**
 * Strategy for propagating the value of collection-typed properties from source to target.
 *
 * @author Sjaak Derksen
 */
enum class CollectionMappingStrategy {
    /**
     * The setter of the target property will be used to propagate the value:
     * `orderDto.setOrderLines(order.getOrderLines)`.
     *
     *
     * If no setter is available but a getter method, this will be used, under the assumption it has been initialized:
     * `orderDto.getOrderLines().addAll(order.getOrderLines)`. This will also be the case when using
     * [MappingTarget] (updating existing instances).
     */
    ACCESSOR_ONLY,

    /**
     * If present, the setter of the target property will be used to propagate the value:
     * `orderDto.setOrderLines(order.getOrderLines)`.
     *
     *
     * If no setter but and adder method is present, that adder will be invoked for each element of the source
     * collection: `order.addOrderLine(orderLine() )`.
     *
     *
     * If neither a setter nor an adder method but a getter for the target property is present, that getter will be
     * used, assuming it returns an initialized collection: If no setter is available, MapStruct will first look for an
     * adder method before resorting to a getter.
     */
    SETTER_PREFERRED,

    /**
     * Identical to [.SETTER_PREFERRED], only that adder methods will be preferred over setter methods, if both
     * are present for a given collection-typed property.
     */
    ADDER_PREFERRED,

    /**
     * Identical to [.SETTER_PREFERRED], however the target collection will not be cleared and accessed via
     * addAll in case of updating existing bean instances, see: [MappingTarget].
     *
     * Instead the target accessor (e.g. set) will be used on the target bean to set the collection.
     */
    TARGET_IMMUTABLE
}

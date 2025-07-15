package org.mapstruct

/**
 * Strategy for dealing with `null` values passed to mapping methods.
 *
 * @author Sjaak Derksen
 */
enum class NullValueMappingStrategy {
    /**
     * If `null` is passed to a mapping method, `null` will be returned. That's the default behavior if no
     * alternative strategy is configured globally, for given mapper or method.
     */
    RETURN_NULL,

    /**
     * If `null` is passed to a mapping method, a default value will be returned. The value depends on the kind of
     * the annotated method:
     *
     *  * For bean mapping methods the target type will be instantiated and returned. Any properties of the target type
     * which are mapped via [Mapping.expression] or [Mapping.constant] will be populated based on the
     * given expression or constant. Note that expressions must be prepared to deal with `null` values in this
     * case.
     *  * For iterable mapping methods an empty collection will be returned.
     *  * For map mapping methods an empty map will be returned.
     *
     */
    RETURN_DEFAULT
}

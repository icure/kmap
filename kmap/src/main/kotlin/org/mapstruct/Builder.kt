package org.mapstruct

import org.mapstruct.util.Experimental


/**
 * Configuration of builders, e.g. the name of the final build method.
 *
 *
 *
 * **Example:** Using builder
 *
 * <pre>`
 * // Mapper
 * &#64;Mapper
 * public interface SimpleBuilderMapper {
 * &#64;Mapping(target = "name", source = "fullName"),
 * &#64;Mapping(target = "job", constant = "programmer"),
 * SimpleImmutablePerson toImmutable(SimpleMutablePerson source);
 * }
`</pre> *
 * <pre>`
 * // generates
 * &#64;Override
 * public SimpleImmutablePerson toImmutable(SimpleMutablePerson source) {
 * // name method can be changed with parameter [.buildMethod]
 * Builder simpleImmutablePerson = SimpleImmutablePerson.builder();
 * simpleImmutablePerson.name( source.getFullName() );
 * simpleImmutablePerson.age( source.getAge() );
 * simpleImmutablePerson.address( source.getAddress() );
 * simpleImmutablePerson.job( "programmer" );
 * // ...
 * }
`</pre> *
 *
 * @author Filip Hrisafov
 *
 * @since 1.3
 */
@Retention(AnnotationRetention.BINARY)
@Target
@Experimental
annotation class Builder(
    /**
     * The name of the build method that needs to be invoked on the builder to create the type to be build
     *
     * @return the method that needs to tbe invoked on the builder
     */
    val buildMethod: String = "build",
    /**
     * Toggling builders on / off. Builders are sometimes used solely for unit testing (fluent testdata)
     * MapStruct will need to use the regular getters /setters in that case.
     *
     * @return when true, no builder patterns will be applied
     */
    val disableBuilder: Boolean = false
)

/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct

/**
 * Marks mapping methods with the given qualifier name. Can be used to qualify a single method or all methods of a given
 * type by specifying this annotation on the type level.
 *
 *
 * Will be used to to select the correct mapping methods when mapping a bean property type, element of an iterable type
 * or the key/value of a map type.
 *
 *
 * Example (both methods of `Titles` are capable to convert a string, but the ambiguity is resolved by applying
 * the qualifiers in `@Mapping`:
 *
 * <pre>
 * `
 * &#64;Named("TitleTranslator")
 * public class Titles {
 *
 * &#64;Named("EnglishToGerman")
 * public String translateTitleEG(String title) {
 * // some mapping logic
 * }
 *
 * &#64;Named("GermanToEnglish")
 * public String translateTitleGE(String title) {
 * // some mapping logic
 * }
 * }
 *
 * &#64;Mapper( uses = Titles.class )
 * public interface MovieMapper {
 *
 * &#64;Mapping( target = "title", qualifiedByName = { "TitleTranslator", "EnglishToGerman" } )
 * GermanRelease toGerman( OriginalRelease movies );
 *
 * }
` *
</pre> *
 *
 * The following implementation of `MovieMapper` will be generated:
 *
 * <pre>
 * `
 *
 * public class MovieMapperImpl implements MovieMapper {
 * private final Titles titles = new Titles();
 *
 * &#64;Override
 * public GermanRelease toGerman(OriginalRelease movies) {
 * if ( movies == null ) {
 * return null;
 * }
 *
 * GermanRelease germanRelease = new GermanRelease();
 *
 * germanRelease.setTitle( titles.translateTitleEG( movies.getTitle() ) );
 *
 * return germanRelease;
 * }
 * }
` *
</pre> *
 *
 * @author Sjaak Derksen
 * @see Mapping.qualifiedByName
 * @see IterableMapping.qualifiedByName
 * @see MapMapping.keyQualifiedByName
 * @see MapMapping.valueQualifiedByName
 */
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class Named(
    /**
     * A name qualifying the annotated element
     *
     * @return the name.
     */
    val value: String
)

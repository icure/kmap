/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct

/**
 * Declares an annotation type to be a qualifier. Qualifier annotations allow unambiguously identify a suitable mapping
 * method in case several methods qualify to map a bean property, iterable element etc.
 *
 *
 * Can be used in:
 *
 *  * [Mapping.qualifiedBy]
 *  * [BeanMapping.qualifiedBy]
 *  * [IterableMapping.qualifiedBy]
 *  * [MapMapping.keyQualifiedBy]
 *  * [MapMapping.valueQualifiedBy]
 *
 *
 * **Example:**
 * <pre>`
 * // create qualifiers
 * &#64;Qualifier
 * &#64;Target(ElementType.TYPE)
 * &#64;Retention(RetentionPolicy.CLASS)
 * public &#64;interface TitleTranslator {}
 *
 * &#64;Qualifier
 * &#64;Target(ElementType.METHOD)
 * &#64;Retention(RetentionPolicy.CLASS)
 * public @interface EnglishToGerman {}
 *
 * &#64;Qualifier
 * &#64;Target(ElementType.METHOD)
 * &#64;Retention(RetentionPolicy.CLASS)
 * public @interface GermanToEnglish {}
`</pre> *
 * <pre>`
 * // we can create class with map methods
 * &#64;TitleTranslator
 * public class Titles {
 * &#64;EnglishToGerman
 * public String translateTitleEnglishToGerman(String title) {
 * // some mapping logic
 * }
 * &#64;GermanToEnglish
 * public String translateTitleGermanToEnglish(String title) {
 * // some mapping logic
 * }
 * }
`</pre> *
 * <pre>`
 * // usage
 * &#64;Mapper( uses = Titles.class )
 * public interface MovieMapper {
 * &#64;Mapping( target = "title", qualifiedBy = { TitleTranslator.class, EnglishToGerman.class } )
 * GermanRelease toGerman( OriginalRelease movies );
 * }
`</pre> *
 * <pre>`
 * // generates
 * public class MovieMapperImpl implements MovieMapper {
 * private final Titles titles = new Titles();
 * &#64;Override
 * public GermanRelease toGerman(OriginalRelease movies) {
 * if ( movies == null ) {
 * return null;
 * }
 * GermanRelease germanRelease = new GermanRelease();
 * germanRelease.setTitle( titles.translateTitleEnglishToGerman( movies.getTitle() ) );
 * return germanRelease;
 * }
 * }
`</pre> *
 *
 * **NOTE:** Qualifiers should have [RetentionPolicy.CLASS].
 *
 * @author Sjaak Derksen
 * @see Named
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Qualifier 

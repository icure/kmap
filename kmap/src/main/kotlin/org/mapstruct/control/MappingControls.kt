/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.control

/**
 * Allows multiple [MappingControl] on a class declaration.
 *
 * @author Sjaak Derksen
 *
 * @since 1.4
 */
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class MappingControls(vararg val value: MappingControl)

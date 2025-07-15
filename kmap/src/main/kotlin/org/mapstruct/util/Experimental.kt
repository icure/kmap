/*
 * Copyright MapStruct Authors.
 *
 * Licensed under the Apache License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package org.mapstruct.util

/**
 * Features that are marked with this annotation are considered *experimental*.
 *
 * @author Andreas Gudian
 */
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
annotation class Experimental(val value: String = "")

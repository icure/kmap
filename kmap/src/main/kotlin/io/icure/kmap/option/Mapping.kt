package io.icure.kmap.option

internal data class Mapping(val target: String? = null, val source: String? = null, val ignore: Boolean = false, val expression: String? = null, val requireMappingContext: Boolean = false)

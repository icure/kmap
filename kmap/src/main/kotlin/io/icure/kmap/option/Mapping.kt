package io.icure.kmap.option

import java.beans.Expression

data class Mapping(val target: String? = null, val source: String? = null, val ignore: Boolean = false, val expression: String? = null)

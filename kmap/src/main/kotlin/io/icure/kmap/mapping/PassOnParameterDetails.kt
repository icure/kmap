package io.icure.kmap.mapping

import com.google.devtools.ksp.symbol.KSType

internal open class PassOnParameterDetails(
	val name: String,
	val type: KSType,
	val matchName: Boolean,
)

internal class DefaultPassOnParameterDetails(
	sourceName: String,
	type: KSType,
	matchName: Boolean,
	val defaultValueExpression: String
) : PassOnParameterDetails(sourceName, type, matchName)
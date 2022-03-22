package com.squareup.kotlinpoet.ksp

import com.google.devtools.ksp.isLocal
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeAlias
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Variance
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.tags.TypeAliasTag

@KotlinPoetKspPreview
fun KSTypeArgument.toTypeName(
    resolvedType: KSType
): TypeName {
    val typeName = type?.toTypeName(resolvedType) ?: return STAR
    return when (variance) {
        Variance.COVARIANT -> WildcardTypeName.producerOf(typeName)
        Variance.CONTRAVARIANT -> WildcardTypeName.consumerOf(typeName)
        Variance.STAR -> STAR
        Variance.INVARIANT -> typeName
    }
}

@KotlinPoetKspPreview
fun KSTypeReference.toTypeName(
    resolvedType: KSType
): TypeName {
    return resolvedType.toTypeName(TypeParameterResolver.EMPTY, element?.typeArguments.orEmpty())
}

@KotlinPoetKspPreview
internal fun KSType.toTypeName(
    typeParamResolver: TypeParameterResolver,
    typeArguments: List<KSTypeArgument>,
): TypeName {
    val type = when (val decl = declaration) {
        is KSClassDeclaration -> {
            decl.toClassName().withTypeArguments(arguments.map { it.toTypeName(typeParamResolver) })
        }
        is KSTypeParameter -> typeParamResolver[decl.name.getShortName()]
        is KSTypeAlias -> {
            val extraResolver = if (decl.typeParameters.isEmpty()) {
                typeParamResolver
            } else {
                decl.typeParameters.toTypeParameterResolver(typeParamResolver)
            }
            val mappedArgs = arguments.map { it.toTypeName(typeParamResolver) }

            val abbreviatedType = decl.type.resolve()
                .toTypeName(extraResolver)
                .copy(nullable = isMarkedNullable)
                .rawType()
                .withTypeArguments(mappedArgs)

            val aliasArgs = typeArguments.map { it.toTypeName(typeParamResolver) }

            decl.toClassNameInternal()
                .withTypeArguments(aliasArgs)
                .copy(tags = mapOf(TypeAliasTag::class to TypeAliasTag(abbreviatedType)))
        }
        else -> error("Unsupported type: $declaration")
    }

    return type.copy(nullable = isMarkedNullable)
}


internal fun TypeName.rawType(): ClassName {
    return findRawType() ?: throw IllegalArgumentException("Cannot get raw type from $this")
}

internal fun TypeName.findRawType(): ClassName? {
    return when (this) {
        is ClassName -> this
        is ParameterizedTypeName -> rawType
        is LambdaTypeName -> {
            var count = parameters.size
            if (receiver != null) {
                count++
            }
            val functionSimpleName = if (count >= 23) {
                "FunctionN"
            } else {
                "Function$count"
            }
            ClassName("kotlin.jvm.functions", functionSimpleName)
        }
        else -> null
    }
}

internal fun ClassName.withTypeArguments(arguments: List<TypeName>): TypeName {
    return if (arguments.isEmpty()) {
        this
    } else {
        this.parameterizedBy(arguments)
    }
}

internal fun KSDeclaration.toClassNameInternal(): ClassName {
    require(!isLocal()) {
        "Local/anonymous classes are not supported!"
    }
    val pkgName = packageName.asString()
    val typesString = checkNotNull(qualifiedName).asString().removePrefix("$pkgName.")

    val simpleNames = typesString
        .split(".")
    return ClassName(pkgName, simpleNames)
}

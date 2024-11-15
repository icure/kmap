package io.icure.kmap

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import io.icure.kmap.exception.ShouldDeferException
import io.icure.kmap.mapping.MapperVisitor
import io.icure.kmap.mapping.mapperAnnotation


class MapperProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("org.mapstruct.Mapper")
        val invalidSymbols = symbols.filter { !it.validate() }.toList() +
            symbols
                .mapNotNull { (it as? KSClassDeclaration)?.takeIf { declaration -> declaration.validate() } }
                .filter { cd ->
                    cd.classKind == ClassKind.INTERFACE || cd.classKind == ClassKind.CLASS && cd.modifiers.contains(
                        Modifier.ABSTRACT
                    )
                }
                .mapNotNull {
                    try {
                        it.accept(MapperVisitor(it.mapperAnnotation(), codeGenerator, logger), Unit)
                        null
                    } catch (e: ShouldDeferException) {
                        logger.warn("Deferring processing of ${it.qualifiedName?.asString()}")
                        it
                    }
                }
        return invalidSymbols
    }
}

class MapperProcessorProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return MapperProcessor(environment.codeGenerator, environment.logger)
    }
}

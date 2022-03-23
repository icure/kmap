package com.example

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring", uses = [BMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AMapper {
    fun map(a1Param:A1):A2
    fun map(a2Param:A2):A1
    @Mappings(Mapping(target ="a", source = "a"))
    fun mapWithMappingsAnnotations(a1Param:A1):A2
    @Mappings(Mapping(target ="aOrB", source = "a1Param"))
    fun mapWithMappingsAnnotationsFromSelf(a1Param:A1):A3

    fun mapOnAorB(a1Param:A1): String = a1Param.a.takeIf { it.isNotEmpty() } ?: a1Param.b.joinToString(",")
}

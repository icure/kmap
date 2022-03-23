package com.example

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper(componentModel = "spring", uses = [BMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AMapper {
    fun map(a:A1):A2
    fun map(a:A2):A1
    @Mappings(Mapping(target ="a", source = "a"))
    fun mapWithMappingsAnnotations(a:A1):A2
}

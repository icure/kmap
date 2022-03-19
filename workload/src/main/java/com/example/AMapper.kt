package com.example

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring", uses = [BMapper::class], injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface AMapper {
    fun map(a:A1):A2
    fun map(a:A2):A1
}

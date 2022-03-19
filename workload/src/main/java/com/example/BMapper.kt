package com.example

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface BMapper {
    fun map(a:B1):B2
    fun map(a:B2):B1
}

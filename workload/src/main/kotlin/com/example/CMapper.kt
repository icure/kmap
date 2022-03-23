package com.example

import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface CMapper {
    fun <O>map(cParam:C1<O>):C2<O>
}

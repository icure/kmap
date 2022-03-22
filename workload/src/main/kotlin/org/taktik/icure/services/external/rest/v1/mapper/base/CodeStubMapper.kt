/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */

package org.taktik.icure.services.external.rest.v1.mapper.base

import org.mapstruct.Mapper
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto

@Mapper(componentModel = "spring")
abstract class CodeStubMapper {
    fun map(codeStubDto: CodeStubDto?): CodeStub? {
        return codeStubDto?.let {
            CodeStub(
                id = it.id ?: "${it.type}|${it.code}|${it.version}",
                type = it.type,
                code = it.code,
                version = it.version,
                context = it.context,
                label = it.label
            )
        }
    }

    fun mapNotNull(codeStubDto: CodeStubDto): CodeStub {
        return codeStubDto.let {
            CodeStub(
                id = it.id ?: "${it.type}|${it.code}|${it.version}",
                type = it.type,
                code = it.code,
                version = it.version,
                context = it.context,
                label = it.label
            )
        }
    }

    abstract fun map(codeStub: CodeStub): CodeStubDto
}
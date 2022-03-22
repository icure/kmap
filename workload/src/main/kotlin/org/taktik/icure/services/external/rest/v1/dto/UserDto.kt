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
package org.taktik.icure.services.external.rest.v1.dto


import org.taktik.icure.services.external.rest.v1.dto.base.PrincipalDto
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationTagDto
import org.taktik.icure.services.external.rest.v1.dto.security.AuthenticationTokenDto
import org.taktik.icure.services.external.rest.v1.dto.security.PermissionDto
import java.io.Serializable
import java.time.Instant

data class UserDto(
         override val id: String,
         override val rev: String? = null,
        override val deletionDate: Long? = null,
        val created: Long? = null,

         override val name: String? = null,
         override val properties: Set<PropertyStubDto> = emptySet(),
         override val permissions: Set<PermissionDto> = emptySet(),
         val roles: Set<String> = emptySet(),
         val login: String? = null,
         val passwordHash: String? = null,
         val secret: String? = null,
         val use2fa: Boolean? = null,
         val groupId: String? = null,
         val healthcarePartyId: String? = null,
         val patientId: String? = null,
         val deviceId: String? = null,
         val autoDelegations: Map<DelegationTagDto, Set<String>> = emptyMap(), //DelegationTagDto -> healthcarePartyIds


         val createdDate: Instant? = null,


         val termsOfUseDate: Instant? = null,
         val email: String? = null,
         val mobilePhone: String? = null,
         val applicationTokens: Map<String, String>? = null,
         val authenticationTokens: Map<String, AuthenticationTokenDto> = emptyMap(),
) : StoredDocumentDto, PrincipalDto, Cloneable, Serializable {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
    override fun getParents(): Set<String> = this.roles
}

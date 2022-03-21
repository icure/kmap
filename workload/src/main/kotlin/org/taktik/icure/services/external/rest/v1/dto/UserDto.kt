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


import org.taktik.icure.constants.Users
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

        @Schema (description = "Last name of the user. This is the official last name that should be used for official administrative purposes.") override val name: String? = null,
        @Schema (description = "Extra properties for the user. Those properties are typed (see class Property)") override val properties: Set<PropertyStubDto> = emptySet(),
        @Schema (description = "If permission to modify patient data is granted or revoked") override val permissions: Set<PermissionDto> = emptySet(),
        @Schema (description = "Roles specified for the user") val roles: Set<String> = emptySet(),
        @Schema (description = "Authorization source for user. 'Database', 'ldap' or 'token'") val type: Users.Type? = null,
        @Schema (description = "State of user's activeness: 'Active', 'Disabled' or 'Registering'") val status: Users.Status? = null,
        @Schema (description = "Username for this user. We encourage using an email address") val login: String? = null,
        @Schema (description = "Hashed version of the password (BCrypt is used for hashing)") val passwordHash: String? = null,
        @Schema (description = "Secret token used to verify 2fa") val secret: String? = null,
        @Schema (description = "Whether the user has activated two factors authentication") val use2fa: Boolean? = null,
        @Schema (description = "id of the group (practice/hospital) the user is member of") val groupId: String? = null,
        @Schema (description = "Id of the healthcare party if the user is a healthcare party.") val healthcarePartyId: String? = null,
        @Schema (description = "Id of the patient if the user is a patient") val patientId: String? = null,
        @Schema (description = "Id of the device if the user is a device") val deviceId: String? = null,
        @Schema (description = "Delegations that are automatically generated client side when a new database object is created by this user") val autoDelegations: Map<DelegationTagDto, Set<String>> = emptyMap(), //DelegationTagDto -> healthcarePartyIds

                
         val createdDate: Instant? = null,

                
         val termsOfUseDate: Instant? = null,
         val email: String? = null,
         val mobilePhone: String? = null,
        @get:Deprecated("Do not use - Use authenticationTokens instead") val applicationTokens: Map<String, String> = emptyMap(),
         val authenticationTokens: Map<String, AuthenticationTokenDto> = emptyMap(),
) : StoredDocumentDto, PrincipalDto, Cloneable, Serializable {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
    @JsonIgnore
    override fun getParents(): Set<String> = this.roles
}

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


import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import org.taktik.icure.services.external.rest.v1.dto.base.EncryptableDto
import org.taktik.icure.services.external.rest.v1.dto.base.ICureDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DocumentLocationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DocumentStatusDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DocumentTypeDto

data class DocumentDto(
         override val id: String,
         override val rev: String? = null,
        override val created: Long? = null,
        override val modified: Long? = null,
        override val author: String? = null,
        override val responsible: String? = null,
        override val medicalLocationId: String? = null,
        override val tags: Set<CodeStubDto> = emptySet(),
        override val codes: Set<CodeStubDto> = emptySet(),
        override val endOfLife: Long? = null,
        override val deletionDate: Long? = null,

         val objectStoreReference: String? = null,
         val documentLocation: DocumentLocationDto? = null,
         val documentType: DocumentTypeDto? = null,
         val documentStatus: DocumentStatusDto? = null,
         val externalUri: String? = null,
         val mainUti: String? = null,
         val name: String? = null,
         val version: String? = null,
         val otherUtis: Set<String> = emptySet(),
         val storedICureDocumentId: String? = null, //The ICureDocumentDto (FormDto, ContactDto, ...) that has been used to generate the document
         val externalUuid: String? = null,
         val size: Long? = null,
         val hash: String? = null,
         val openingContactId: String? = null,

         val attachmentId: String? = null,

        @Schema(type = "string", format = "byte") val encryptedAttachment: ByteArray? = null,
        @Schema(type = "string", format = "byte") val decryptedAttachment: ByteArray? = null,

        override val secretForeignKeys: Set<String> = emptySet(),
        override val cryptedForeignKeys: Map<String, Set<DelegationDto>> = emptyMap(),
        override val delegations: Map<String, Set<DelegationDto>> = emptyMap(),
        override val encryptionKeys: Map<String, Set<DelegationDto>> = emptyMap(),

        override val encryptedSelf: String? = null
) : StoredDocumentDto, ICureDocumentDto<String>, EncryptableDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

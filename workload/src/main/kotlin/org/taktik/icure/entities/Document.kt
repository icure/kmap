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
package org.taktik.icure.entities

import org.taktik.couchdb.entity.Attachment
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.Encryptable
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.DataAttachment
import org.taktik.icure.entities.embed.Delegation
import org.taktik.icure.entities.embed.DeletedAttachment
import org.taktik.icure.entities.embed.DocumentLocation
import org.taktik.icure.entities.embed.DocumentStatus
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke


data class Document(
    override val id: String,
    override val rev: String? = null,
    override val created: Long? = null,
    override val modified: Long? = null,
    override val author: String? = null,
    override val responsible: String? = null,
    override val medicalLocationId: String? = null,
    override val tags: Set<CodeStub> = emptySet(),
    override val codes: Set<CodeStub> = emptySet(),
    override val endOfLife: Long? = null,
    override val deletionDate: Long? = null,
    val size: Long? = null,
    val hash: String? = null,
    val openingContactId: String? = null,
    val documentLocation: DocumentLocation? = null,
    val documentType: DocumentType? = null,
    val documentStatus: DocumentStatus? = null,
    val externalUri: String? = null,
    val name: String? = null,
    val version: String? = null,
    val storedICureDocumentId: String? = null, //The ICureDocument (Form, Contact, ...) that has been used to generate the document
    val externalUuid: String? = null,

    val attachmentId: String? = null,
    val objectStoreReference: String? = null,
    val mainUti: String? = null,
    val otherUtis: Set<String> = emptySet(),
    val secondaryAttachments: Map<String, DataAttachment> = emptyMap(),
    override val deletedAttachments: List<DeletedAttachment> = emptyList(),

    override val secretForeignKeys: Set<String> = emptySet(),
    override val cryptedForeignKeys: Map<String, Set<Delegation>> = emptyMap(),
    override val delegations: Map<String, Set<Delegation>> = emptyMap(),
    override val encryptionKeys: Map<String, Set<Delegation>> = emptyMap(),
    override val encryptedSelf: String? = null,

    val attachments: Map<String, Attachment>? = emptyMap(),
    override val revisionsInfo: List<RevisionInfo>? = emptyList(),
    override val conflicts: List<String>? = emptyList(),
    override val revHistory: Map<String, String>? = emptyMap()

) : StoredICureDocument, Encryptable, HasDataAttachments<Document> {
    companion object : DynamicInitializer<Document>

    val mainAttachmentKey: String get() = id

    val mainAttachment: DataAttachment? by lazy {
        if (attachmentId != null || objectStoreReference != null)
            DataAttachment(
                attachmentId,
                objectStoreReference,
                listOfNotNull(mainUti) + (mainUti?.let { otherUtis - it } ?: otherUtis)
            )
        else
            null
    }

    override val dataAttachments: Map<String, DataAttachment> by lazy {
        mainAttachment?.let { secondaryAttachments + (mainAttachmentKey to it) } ?: secondaryAttachments
    }

    override fun withUpdatedDataAttachment(key: String, newValue: DataAttachment?): Document =
        if (key == mainAttachmentKey) {
            withUpdatedMainAttachment(newValue)
        } else if (newValue != null) {
            copy(secondaryAttachments = secondaryAttachments + (key to newValue))
        } else {
            copy(secondaryAttachments = secondaryAttachments - key)
        }

    override fun withDataAttachments(newDataAttachments: Map<String, DataAttachment>): Document = this
        .copy(secondaryAttachments = newDataAttachments.filter { it.key != mainAttachmentKey })
        .withUpdatedMainAttachment(newDataAttachments[mainAttachmentKey])

    override fun withDeletedAttachments(newDeletedAttachments: List<DeletedAttachment>): Document =
        copy(deletedAttachments = newDeletedAttachments)

    fun merge(other: Document) = Document(args = this.solveConflictsWith(other))

    fun solveConflictsWith(other: Document) = super<StoredICureDocument>.solveConflictsWith(other) + super<Encryptable>.solveConflictsWith(other) + mapOf(
        "size" to (this.size ?: other.size),
        "hash" to (this.hash ?: other.hash),
        "openingContactId" to (this.openingContactId ?: other.openingContactId),
        "documentLocation" to (this.documentLocation ?: other.documentLocation),
        "documentType" to (this.documentType ?: other.documentType),
        "documentStatus" to (this.documentStatus ?: other.documentStatus),
        "externalUri" to (this.externalUri ?: other.externalUri),
        "name" to (this.name ?: other.name),
        "version" to (this.version ?: other.version),
        "storedICureDocumentId" to (this.storedICureDocumentId ?: other.storedICureDocumentId),
        "externalUuid" to (this.externalUuid ?: other.externalUuid),
        "deletedAttachments" to this.solveDeletedAttachmentsConflicts(other),
    ) + this.solveDataAttachmentsConflicts(other).let { allDataAttachments ->
        allDataAttachments[this.mainAttachmentKey].let { mainAttachment ->
            mapOf(
                "attachmentId" to mainAttachment?.couchDbAttachmentId,
                "objectStoreReference" to mainAttachment?.objectStoreAttachmentId,
                "mainUti" to mainUtiOf(mainAttachment),
                "otherUtis" to otherUtisOf(mainAttachment),
                "secondaryAttachments" to (allDataAttachments - this.mainAttachmentKey)
            )
        }
    }

    override fun withIdRev(id: String?, rev: String) =
        if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)

    override fun withDeletionDate(deletionDate: Long?) =
        this.copy(deletionDate = deletionDate)

    override fun withTimestamps(created: Long?, modified: Long?) =
        when {
            created != null && modified != null -> this.copy(created = created, modified = modified)
            created != null -> this.copy(created = created)
            modified != null -> this.copy(modified = modified)
            else -> this
        }

    fun withUpdatedMainAttachment(newMainAttachment: DataAttachment?) =
        this.copy(
            attachmentId = newMainAttachment?.couchDbAttachmentId,
            objectStoreReference = newMainAttachment?.objectStoreAttachmentId,
            mainUti = mainUtiOf(newMainAttachment),
            otherUtis = otherUtisOf(newMainAttachment),
        )

    private fun mainUtiOf(mainAttachment: DataAttachment?) =
        mainAttachment?.utis?.firstOrNull()

    private fun otherUtisOf(mainAttachment: DataAttachment?) =
        mainAttachment?.utis?.drop(1)?.toSet() ?: emptySet()
}

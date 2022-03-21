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

import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.base.ReportVersion
import org.taktik.icure.entities.base.StoredICureDocument
import org.taktik.icure.entities.embed.DocumentGroup
import org.taktik.icure.entities.embed.DocumentType
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke

data class DocumentTemplate(
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

        val attachment: ByteArray? = null,
        var isAttachmentDirty: Boolean = false,
        val mainUti: String? = null,
        val name: String? = null,
        val otherUtis: Set<String> = emptySet(),
        val attachmentId: String? = null,
        val version: ReportVersion? = null,
        val owner: String? = null,
        val guid: String? = null,
        val group: DocumentGroup? = null,
        val descr: String? = null,
        val disabled: String? = null,
        val specialty: CodeStub? = null,
        val documentType: DocumentType? = null,

       override val revisionsInfo: List<RevisionInfo>? = emptyList(),
       override val conflicts: List<String>? = emptyList(),
       override val revHistory: Map<String, String>? = emptyMap()

) : StoredICureDocument {
    companion object : DynamicInitializer<DocumentTemplate>

    fun merge(other: DocumentTemplate) = DocumentTemplate(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: DocumentTemplate) = super<StoredICureDocument>.solveConflictsWith(other) + mapOf(
            "mainUti" to (this.mainUti ?: other.mainUti),
            "name" to (this.name ?: other.name),
            "otherUtis" to (other.otherUtis + this.otherUtis),
            "attachmentId" to (this.attachmentId ?: other.attachmentId),
            "version" to (this.version ?: other.version),
            "owner" to (this.owner ?: other.owner),
            "guid" to (this.guid ?: other.guid),
            "group" to (this.group ?: other.group),
            "descr" to (this.descr ?: other.descr),
            "disabled" to (this.disabled ?: other.disabled),
            "specialty" to (this.specialty ?: other.specialty),
            "attachment" to (this.attachment?.let { if (it.size >= other.attachment?.size ?: 0) it else other.attachment }
                    ?: other.attachment),
            "documentType" to (this.documentType ?: other.documentType)
    )

    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
    override fun withTimestamps(created: Long?, modified: Long?) =
            when {
                created != null && modified != null -> this.copy(created = created, modified = modified)
                created != null -> this.copy(created = created)
                modified != null -> this.copy(modified = modified)
                else -> this
            }

}

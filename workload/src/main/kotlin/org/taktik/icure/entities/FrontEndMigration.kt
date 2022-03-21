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

import org.taktik.icure.entities.base.PropertyStub
import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.FrontEndMigrationStatus
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke

data class FrontEndMigration(
       override val id: String,
       override val rev: String? = null,
       override val deletionDate: Long? = null,

        val name: String? = null,
        val startDate: Long? = null,
        val endDate: Long? = null,
        val status: FrontEndMigrationStatus? = null,
        val logs: String? = null,
        val userId: String? = null,
        val startKey: String? = null,
        val startKeyDocId: String? = null,
        val processCount: Long? = null,
        val properties: Set<PropertyStub> = emptySet(),

       override val revisionsInfo: List<RevisionInfo>? = emptyList(),
       override val conflicts: List<String>? = emptyList(),
       override val revHistory: Map<String, String>? = emptyMap()

) : StoredDocument {
    companion object : DynamicInitializer<FrontEndMigration>

    fun merge(other: FrontEndMigration) = FrontEndMigration(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: FrontEndMigration) = super.solveConflictsWith(other) + mapOf(
            "name" to (this.name ?: other.name),
            "startDate" to (this.startDate ?: other.startDate),
            "endDate" to (this.endDate ?: other.endDate),
            "status" to (this.status ?: other.status),
            "logs" to (this.logs ?: other.logs),
            "userId" to (this.userId ?: other.userId),
            "startKey" to (this.startKey ?: other.startKey),
            "startKeyDocId" to (this.startKeyDocId ?: other.startKeyDocId),
            "processCount" to (this.processCount ?: other.processCount),
            "properties" to (other.properties + this.properties),
    )

    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

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

import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.Encrypted
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke

data class Property(
       override val id: String,
       override val rev: String? = null,
       override val deletionDate: Long? = null,

        val type: PropertyType? = null,
        val typedValue: String? = null,
        override val encryptedSelf: String? = null,

       override val revisionsInfo: List<RevisionInfo>? = emptyList(),
       override val conflicts: List<String>? = emptyList(),
       override val revHistory: Map<String, String>? = emptyMap()
) : StoredDocument, Encrypted {
    companion object : DynamicInitializer<Property>

    fun merge(other: Property) = Property(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: Property) = super<StoredDocument>.solveConflictsWith(other) + super<Encrypted>.solveConflictsWith(other) + mapOf(
            "type" to (this.type ?: other.type),
            "typedValue" to (this.typedValue ?: other.typedValue)
    )

    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

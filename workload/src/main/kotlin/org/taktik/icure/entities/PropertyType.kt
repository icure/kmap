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
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.utils.DynamicInitializer
import org.taktik.icure.utils.invoke

data class PropertyType(
       override val id: String,
       override val rev: String? = null,
       override val deletionDate: Long? = null,

        val identifier: String? = null,
        val type: String? = null,
        val unique: Boolean = false,
        val editor: String? = null,
        val localized: Boolean = false,

       override val revisionsInfo: List<RevisionInfo>? = emptyList(),
       override val conflicts: List<String>? = emptyList(),
       override val revHistory: Map<String, String>? = emptyMap()

) : StoredDocument {
    companion object : DynamicInitializer<PropertyType> {
        fun with(type: String, identifier: String) = PropertyType(id = identifier, type = type, identifier = identifier)
    }

    fun merge(other: PropertyType) = PropertyType(args = this.solveConflictsWith(other))
    fun solveConflictsWith(other: PropertyType) = super.solveConflictsWith(other) + mapOf(
            "identifier" to (this.identifier ?: other.identifier),
            "type" to (this.type ?: other.type),
            "unique" to (this.unique),
            "editor" to (this.editor ?: other.editor),
            "localized" to (this.localized)
    )

    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

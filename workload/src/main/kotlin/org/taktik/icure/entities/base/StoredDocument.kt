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
package org.taktik.icure.entities.base

import org.taktik.icure.entities.embed.RevisionInfo

interface StoredDocument : Versionable<String> {
    @Suppress("PropertyName")
    fun getJavaType(): String {
        return this::class.qualifiedName!!
    }
    fun setJavaType(value: String) {
        if (this::class.qualifiedName != value) throw IllegalArgumentException("Object with ID ${this.id} is not of expected type ${this::class.qualifiedName} but of type $value")
    }

    val deletionDate: Long?
    val revisionsInfo: List<RevisionInfo>?
    val conflicts: List<String>?

    fun solveConflictsWith(other: StoredDocument): Map<String, Any?> {
        return mapOf(
                "id" to this.id,
                "rev" to this.rev,
                "revHistory" to (other.revHistory?.let { it + (this.revHistory ?: mapOf()) } ?: this.revHistory),
                "revisionsInfo" to this.revisionsInfo,
                "conflicts" to this.conflicts,
                "deletionDate" to (this.deletionDate ?: other.deletionDate)
        )
    }

    fun withDeletionDate(deletionDate: Long?): StoredDocument
}

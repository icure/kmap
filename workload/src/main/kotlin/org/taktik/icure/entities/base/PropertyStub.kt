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

import org.taktik.icure.entities.embed.Encrypted
import org.taktik.icure.entities.embed.TypedValue
import java.io.Serializable

data class PropertyStub(
        val id: String? = null,
        val type: PropertyTypeStub? = null,
        val typedValue: TypedValue<*>? = null,
        @Deprecated("Remove from list instead")val deletionDate: Long? = null,
        override val encryptedSelf: String? = null
) : Serializable, Encrypted {
    @JsonIgnore
    fun <T> getValue(): T? {
        return (typedValue?.getValue<Any>()?.let { it as? T })
    }
}

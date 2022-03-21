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
package org.taktik.icure.entities.embed

import java.io.Serializable
import java.time.Instant
import java.util.*

data class TypedValue<T>(
        val type: String? = null,
        val booleanValue: Boolean? = null,
        val integerValue: Int? = null,
        val doubleValue: Double? = null,
        val stringValue: String? = null,


        val dateValue: Instant? = null,
        override val encryptedSelf: String? = null
) : Comparable<TypedValue<T>>, Encrypted, Serializable {
    companion object {
        fun <T> withValue(value: T?): TypedValue<T>? = value?.let {
            withTypeAndValue(when (value) {
                is Boolean -> String.BOOLEAN
                is Int -> String.INTEGER
                is Double -> String.DOUBLE
                is String -> String.STRING
                is Date -> String.DATE
                else -> throw IllegalArgumentException("Unknown value type")
            }, value)
        }

        fun <T> withTypeAndValue(type: String, value: T?): TypedValue<T>? = value?.let {
            when (type) {
                String.BOOLEAN -> if (value is Boolean) {
                    TypedValue(booleanValue = value, type = type)
                } else throw IllegalArgumentException("Illegal boolean value")
                String.INTEGER -> if (value is Int) {
                    TypedValue(integerValue = value, type = type)
                } else throw IllegalArgumentException("Illegal integer value")
                String.DOUBLE -> if (value is Double) {
                    TypedValue(doubleValue = value, type = type)
                } else throw IllegalArgumentException("Illegal double value")
                String.STRING, String.JSON, String.CLOB -> if (value is String) {
                    TypedValue(stringValue = value, type = type)
                } else throw IllegalArgumentException("Illegal string value")
                String.DATE -> if (value is Instant) {
                    TypedValue(dateValue = value, type = type)
                } else if (value is Date) {
                    TypedValue(dateValue = (value as Date).toInstant(), type = type)
                } else throw IllegalArgumentException("Illegal date value")
            }
        }
    }

    @JsonIgnore
    fun <T> getValue(): T? {
        if (type == null) {
            return null
        }
        return when (type) {
            String.BOOLEAN -> booleanValue as? T
            String.INTEGER -> integerValue as? T
            String.DOUBLE -> doubleValue as? T
            String.STRING, String.CLOB, String.JSON -> stringValue as? T
            String.DATE -> dateValue as? T
        }
    }

    override fun compareTo(other: TypedValue<T>): Int {
        return (other.getValue<T>() as Comparable<T>).compareTo(getValue<T>()!!)
    }

    override fun toString(): String {
        if (type != null) {
            when (type) {
                String.BOOLEAN -> return booleanValue.toString()
                String.INTEGER -> return integerValue.toString()
                String.DOUBLE -> return doubleValue.toString()
                String.STRING, String.CLOB, String.JSON -> return stringValue!!
                String.DATE -> return dateValue.toString()
            }
        }
        return super.toString()
    }
}

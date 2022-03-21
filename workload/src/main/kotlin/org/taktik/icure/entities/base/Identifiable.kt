package org.taktik.icure.entities.base

import java.io.Serializable

interface Identifiable<T> : Serializable {
    val id: T
}

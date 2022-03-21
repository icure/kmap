package org.taktik.icure.entities.base

interface Versionable<T> : Identifiable<T> {
    val revHistory: Map<String, String>?
    val rev: String?

    fun withIdRev(id: T? = null, rev: String): Versionable<T>

}

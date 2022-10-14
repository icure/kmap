package org.taktik.couchdb.entity

class Attachment(
    val id: String? = null,
    val contentType: String? = null,
    val contentLength: Long = 0,
    val dataBase64: String? = null,
    val isStub: Boolean = false,
    val revpos: Int = 0,
    val digest: String? = null,
    val length: Long? = null
)
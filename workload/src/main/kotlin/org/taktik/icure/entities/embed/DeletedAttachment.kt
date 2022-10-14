package org.taktik.icure.entities.embed

import java.io.Serializable

data class DeletedAttachment(
    val couchDbAttachmentId: String? = null,
    val objectStoreAttachmentId: String? = null,
    val key: String? = null,
    val deletionTime: Long? = null
) : Serializable

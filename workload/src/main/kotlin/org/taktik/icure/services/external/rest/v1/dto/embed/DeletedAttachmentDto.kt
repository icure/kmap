package org.taktik.icure.services.external.rest.v1.dto.embed

import java.io.Serializable

data class DeletedAttachmentDto(
    val couchDbAttachmentId: String? = null,

    val objectStoreAttachmentId: String? = null,

    val key: String? = null,

    val deletionTime: Long? = null
) : Serializable

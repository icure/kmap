package org.taktik.icure.services.external.rest.v1.dto.embed

import java.io.Serializable

data class DataAttachmentDto(
    val couchDbAttachmentId: String? = null,
    val objectStoreAttachmentId: String? = null,
    val utis: List<String> = emptyList()
) : Serializable

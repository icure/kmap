package org.taktik.icure.services.external.rest.v1.dto


data class ReplicateCommandDto(
       val sourceUrl: String,
       val sourceUsername: String,
       val sourcePassword: String,
       val targetUrl: String,
       val targetUsername: String,
       val targetPassword: String,
        val id: String? = null
)

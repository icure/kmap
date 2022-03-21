package org.taktik.icure.services.external.rest.v1.dto


data class RegistrationSuccessDto(
        val groupId: String,
        val userId: String,
        val token: String
)

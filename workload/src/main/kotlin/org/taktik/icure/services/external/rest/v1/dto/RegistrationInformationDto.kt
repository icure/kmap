package org.taktik.icure.services.external.rest.v1.dto


data class RegistrationInformationDto(
    val firstName: String,
    val lastName: String,
    val emailAddress: String,
    val userOptions: String? = null
)

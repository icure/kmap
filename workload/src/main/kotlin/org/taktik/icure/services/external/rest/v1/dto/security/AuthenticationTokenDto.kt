package org.taktik.icure.services.external.rest.v1.dto.security

import java.io.Serializable
import java.time.Instant

data class AuthenticationTokenDto(
         val token: String,
         val creationTime: Long = Instant.now().toEpochMilli(),
         val validity: Long
) : Cloneable, Serializable

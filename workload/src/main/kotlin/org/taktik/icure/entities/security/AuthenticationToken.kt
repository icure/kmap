package org.taktik.icure.entities.security

import java.io.Serializable
import java.time.Instant

/**
 * Token used for inter-applications authentication. Always as a period of validity before to expire
 * @property token Encrypted token
 * @property creationTime Validity starting time of the token
 * @property validity Token validity in seconds. If no validity is passed, then the token never expires. (Retro compatibility for applicationTokens)
 */
data class AuthenticationToken(
        val token: String,


        val creationTime: Instant = Instant.now(),

        val validity: Long = 3600
) : Cloneable, Serializable {
    companion object {
        const val LONG_LIVING_TOKEN_VALIDITY = -1L
    }

}


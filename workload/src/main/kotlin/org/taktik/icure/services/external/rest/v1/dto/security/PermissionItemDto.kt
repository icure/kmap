package org.taktik.icure.services.external.rest.v1.dto.security

import java.io.Serializable

interface PermissionItemDto : Cloneable, Serializable {
    val type: PermissionTypeDto
}

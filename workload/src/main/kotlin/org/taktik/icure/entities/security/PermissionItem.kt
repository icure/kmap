package org.taktik.icure.entities.security

import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.taktik.icure.domain.filter.predicate.Predicate
import java.io.Serializable

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "java_type")
interface PermissionItem : Cloneable, Serializable {
    val type: PermissionType
    val predicate: Predicate

    fun merge(other: PermissionItem): PermissionItem
}

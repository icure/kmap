package org.taktik.icure.services.external.rest.v1.dto.embed

import org.taktik.icure.services.external.rest.v1.dto.base.IdentifiableDto
import java.util.*

data class AnnotationDto(
         override val id: String = UUID.randomUUID().toString(),
        val author: String? = null,
        val created: Long? = null,
        val modified: Long? = null,
        val text: String? = null,
        val location: String? = null
) : IdentifiableDto<String> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AnnotationDto

        if (id != other.id) return false
        if (author != other.author) return false
        if (created != other.created) return false
        if (modified != other.modified) return false
        if (text != other.text) return false
        if (location != other.location) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + (created?.hashCode() ?: 0)
        result = 31 * result + (modified?.hashCode() ?: 0)
        result = 31 * result + (text?.hashCode() ?: 0)
        result = 31 * result + (location?.hashCode() ?: 0)
        return result
    }
}

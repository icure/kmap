package org.taktik.icure.entities.embed


import java.io.Serializable

/**
 * Represent an attachment holding some additional data for an entity.
 * At least one of [couchDbAttachmentId] or [objectStoreAttachmentId] is always not null.
 * @property couchDbAttachmentId if the attachment is stored as a couchdb attachment this holds the id of the attachment, else null.
 * @property objectStoreAttachmentId if the attachment is stored with the object storage service this holds the id of the attachment, else null.
 * @property utis [Uniform Type Identifiers](https://developer.apple.com/library/archive/documentation/FileManagement/Conceptual/understanding_utis/understand_utis_conc/understand_utis_conc.html#//apple_ref/doc/uid/TP40001319-CH202-CHDHIJDE) for the data attachment.
 * This is a list in order to allow specifying a priority, but each uti must be unique.
 */
data class DataAttachment(
    val couchDbAttachmentId: String? = null,
    val objectStoreAttachmentId: String? = null,
    val utis: List<String> = emptyList()
) : Serializable {
    init {
        require(couchDbAttachmentId != null || objectStoreAttachmentId != null) {
            "Must specify the id of at least one storage place for the attachment"
        }
        require(utis.distinct().size == utis.size) {
            val duplicates = utis.groupingBy { it }.eachCount().filter { it.value > 1 }.toList()
            "There are duplicate utis: $duplicates"
        }
    }

    companion object {
        /**
         * Default mime type for data attachments, if no specific uti is provided.
         */
        const val DEFAULT_MIME_TYPE = "application/xml"
    }

    private var cachedBytes: ByteArray? = null

    val ids: Pair<String?, String?> get() = couchDbAttachmentId to objectStoreAttachmentId

    /**
     * @return if this and other attachment have the same ids (the attachment is the same and is stored in the same place)
     */
    infix fun hasSameIdsAs(other: DataAttachment) =
        this.ids == other.ids

    /**
     * @return a copy of this data attachment where the ids are replaced with those of another data attachment
     */
    fun withIdsOf(other: DataAttachment) = copy(
        couchDbAttachmentId = other.couchDbAttachmentId,
        objectStoreAttachmentId = other.objectStoreAttachmentId
    )

}
package org.taktik.icure.entities.samv2

import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo

data class Paragraph(
        val chapterName: String? = null,
        val paragraphName: String? = null,
        val startDate: Long? = null,
        val createdTms: Long? = null,
        val createdUserId: String? = null,
        val endDate: Long? = null,
        val keyStringNl: String? = null,
        val keyStringFr: String? = null,
        val agreementType: String? = null,
        val processType: Long? = null,
        val legalReference: String? = null,
        val publicationDate: Long? = null,
        val modificationDate: Long? = null,
        val processTypeOverrule: String? = null,
        val paragraphVersion: Long? = null,
        val agreementTypePro: String? = null,
        val modificationStatus: String? = null,

       override val id: String,
       override val rev: String? = null,
       override val deletionDate: Long? = null,
       override val revisionsInfo: List<RevisionInfo>? = emptyList(),
       override val conflicts: List<String>? = emptyList(),
       override val revHistory: Map<String, String>? = emptyMap()
) : StoredDocument {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}
    /*List<Verse> getVerses() {;
         return Verse.findAllByChapterNameAndParagraphName(chapterName,paragraphName,[sort:'verseSeq',order:'asc']);;
     };*/

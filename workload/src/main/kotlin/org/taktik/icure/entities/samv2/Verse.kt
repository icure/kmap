package org.taktik.icure.entities.samv2

import org.taktik.icure.entities.base.StoredDocument
import org.taktik.icure.entities.embed.RevisionInfo
import org.taktik.icure.entities.samv2.embed.AddedDocument

data class Verse(
        val chapterName: String? = null,
        val paragraphName: String? = null,
        val verseSeq: Long? = null,
        val startDate: Long? = null,
        val createdTms: Long? = null,
        val createdUserId: String? = null,
        val endDate: Long? = null,
        val verseNum: Long? = null,
        val verseSeqParent: Long? = null,
        val verseLevel: Long? = null,
        val verseType: String? = null,
        val checkBoxInd: String? = null,
        val minCheckNum: Long? = null,
        val andClauseNum: Long? = null,
        val textFr: String? = null,
        val textNl: String? = null,
        val requestType: String? = null,
        val agreementTerm: Long? = null,
        val agreementTermUnit: String? = null,
        val maxPackageNumber: Long? = null,
        val purchasingAdvisorQualList: String? = null,
        val legalReference: String? = null,
        val modificationDate: Long? = null,
        val addedDocuments: List<AddedDocument> = emptyList(),
        val agreementYearMax: Long? = null,
        val agreementRenewalMax: Long? = null,
        val sexRestricted: String? = null,
        val minimumAgeAuthorized: Double? = null,
        val maximumAgeAuthorized: Double? = null,
        val maximumContentQuantity: Double? = null,
        val maximumContentUnit: String? = null,
        val maximumStrengthQuantity: Double? = null,
        val maximumStrengthUnit: String? = null,
        val maximumDurationQuantity: Double? = null,
        val maximumDurationUnit: String? = null,
        val otherAddedDocumentInd: String? = null,
        val minimumAgeAuthorizedUnit: String? = null,
        val maximumAgeAuthorizedUnit: String? = null,
        val modificationStatus: String? = null,

        @JsonIgnore
        val children: List<Verse>? = null,

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

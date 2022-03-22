/**
 * iCure Data Stack API Documentation
 *
 * The iCure Data Stack Application API is the native interface to iCure.
 *
 * The version of the OpenAPI document: v2
 *
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 */
package io.icure.kraken.client.models

/**
 * This entity is a root level object. It represents a healthcare element. It is serialized in JSON and saved in the underlying CouchDB database.
 *
 * @param id The Id of the healthcare element. We encourage using either a v4 UUID or a HL7 Id.
 * @param identifiers
 * @param tags A tag is an item from a codification system that qualifies an entity as being member of a certain class, whatever the value it might have taken. If the tag qualifies the content of a field, it means that whatever the content of the field, the tag will always apply. For example, the label of a field is qualified using a tag. LOINC is a codification system typically used for tags.
 * @param codes A code is an item from a codification system that qualifies the content of this entity. SNOMED-CT, ICPC-2 or ICD-10 codifications systems can be used for codes
 * @param relevant If the healthcare element is relevant or not (Set relevant by default).
 * @param status bit 0: active/inactive, bit 1: relevant/irrelevant, bit 2 : present/absent, ex: 0 = active,relevant and present
 * @param plansOfAction List of healthcare approaches.
 * @param episodes List of episodes of occurrences of the healthcare element.
 * @param careTeam List of care team members assigned for the healthcare element.
 * @param secretForeignKeys The secretForeignKeys are filled at the to many end of a one to many relationship (for example inside Contact for the Patient -> Contacts relationship). Used when we want to find all contacts for a specific patient. These keys are in clear. You can have several to partition the medical document space.
 * @param cryptedForeignKeys The secretForeignKeys are filled at the to many end of a one to many relationship (for example inside Contact for the Patient -> Contacts relationship). Used when we want to find the patient for a specific contact. These keys are the encrypted id (using the hcParty key for the delegate) that can be found in clear inside the patient. ids encrypted using the hcParty keys.
 * @param delegations When a document is created, the responsible generates a cryptographically random master key (never to be used for something else than referencing from other entities). He/she encrypts it using his own AES exchange key and stores it as a delegation. The responsible is thus always in the delegations as well
 * @param encryptionKeys When a document needs to be encrypted, the responsible generates a cryptographically random master key (different from the delegation key, never to appear in clear anywhere in the db. He/she encrypts it using his own AES exchange key and stores it as a delegation
 * @param rev The revision of the patient in the database, used for conflict management / optimistic locking.
 * @param created The timestamp (unix epoch in ms) of creation of this entity, will be filled automatically if missing. Not enforced by the application server.
 * @param modified The date (unix epoch in ms) of the latest modification of this entity, will be filled automatically if missing. Not enforced by the application server.
 * @param author The id of the User that has created this entity, will be filled automatically if missing. Not enforced by the application server.
 * @param responsible The id of the HealthcareParty that is responsible for this entity, will be filled automatically if missing. Not enforced by the application server.
 * @param medicalLocationId The id of the medical location where this entity was created.
 * @param endOfLife Soft delete (unix epoch in ms) timestamp of the object.
 * @param deletionDate hard delete (unix epoch in ms) timestamp of the object. Filled automatically when deletePatient is called.
 * @param healthElementId The logical id of the healthcare element, used to link together different versions of the same healthcare element. We encourage using either a v4 UUID or a HL7 Id.
 * @param valueDate The date (unix epoch in ms) when the healthcare element is noted to have started and also closes on the same date
 * @param openingDate The date (unix epoch in ms) of the start of the healthcare element.
 * @param closingDate The date (unix epoch in ms) marking the end of the healthcare element.
 * @param descr Description of the healthcare element.
 * @param note A text note (can be confidential, encrypted by default).
 * @param idOpeningContact Id of the opening contact when the healthcare element was created.
 * @param idClosingContact Id of the closing contact for the healthcare element.
 * @param idService Id of the service when a service is used to create a healthcare element.
 * @param laterality Left or Right dominance/preference.
 * @param encryptedSelf The base64 encoded data of this object, formatted as JSON and encrypted in AES using the random master key from encryptionKeys.
 */

data class HealthElementDto (

    /* The Id of the healthcare element. We encourage using either a v4 UUID or a HL7 Id. */

    val id: kotlin.String,


    val identifiers: kotlin.collections.List<IdentifierDto> = emptyList(),

    /* A tag is an item from a codification system that qualifies an entity as being member of a certain class, whatever the value it might have taken. If the tag qualifies the content of a field, it means that whatever the content of the field, the tag will always apply. For example, the label of a field is qualified using a tag. LOINC is a codification system typically used for tags. */

    val tags: kotlin.collections.List<CodeStubDto> = emptyList(),

    /* A code is an item from a codification system that qualifies the content of this entity. SNOMED-CT, ICPC-2 or ICD-10 codifications systems can be used for codes */

    val codes: kotlin.collections.List<CodeStubDto> = emptyList(),

    /* If the healthcare element is relevant or not (Set relevant by default). */

    val relevant: kotlin.Boolean,

    /* bit 0: active/inactive, bit 1: relevant/irrelevant, bit 2 : present/absent, ex: 0 = active,relevant and present */

    val status: kotlin.Int,

    /* List of healthcare approaches. */

    val plansOfAction: kotlin.collections.List<PlanOfActionDto> = emptyList(),

    /* List of episodes of occurrences of the healthcare element. */

    val episodes: kotlin.collections.List<EpisodeDto> = emptyList(),

    /* List of care team members assigned for the healthcare element. */

    val careTeam: kotlin.collections.List<CareTeamMemberDto> = emptyList(),

    /* The secretForeignKeys are filled at the to many end of a one to many relationship (for example inside Contact for the Patient -> Contacts relationship). Used when we want to find all contacts for a specific patient. These keys are in clear. You can have several to partition the medical document space. */

    val secretForeignKeys: kotlin.collections.List<kotlin.String> = emptyList(),

    /* The secretForeignKeys are filled at the to many end of a one to many relationship (for example inside Contact for the Patient -> Contacts relationship). Used when we want to find the patient for a specific contact. These keys are the encrypted id (using the hcParty key for the delegate) that can be found in clear inside the patient. ids encrypted using the hcParty keys. */

    val cryptedForeignKeys: kotlin.collections.Map<kotlin.String, kotlin.collections.Set<DelegationDto>> = emptyMap(),

    /* When a document is created, the responsible generates a cryptographically random master key (never to be used for something else than referencing from other entities). He/she encrypts it using his own AES exchange key and stores it as a delegation. The responsible is thus always in the delegations as well */

    val delegations: kotlin.collections.Map<kotlin.String, kotlin.collections.Set<DelegationDto>> = emptyMap(),

    /* When a document needs to be encrypted, the responsible generates a cryptographically random master key (different from the delegation key, never to appear in clear anywhere in the db. He/she encrypts it using his own AES exchange key and stores it as a delegation */

    val encryptionKeys: kotlin.collections.Map<kotlin.String, kotlin.collections.Set<DelegationDto>> = emptyMap(),

    /* The revision of the patient in the database, used for conflict management / optimistic locking. */

    val rev: kotlin.String? = null,

    /* The timestamp (unix epoch in ms) of creation of this entity, will be filled automatically if missing. Not enforced by the application server. */

    val created: kotlin.Long? = null,

    /* The date (unix epoch in ms) of the latest modification of this entity, will be filled automatically if missing. Not enforced by the application server. */

    val modified: kotlin.Long? = null,

    /* The id of the User that has created this entity, will be filled automatically if missing. Not enforced by the application server. */

    val author: kotlin.String? = null,

    /* The id of the HealthcareParty that is responsible for this entity, will be filled automatically if missing. Not enforced by the application server. */

    val responsible: kotlin.String? = null,

    /* The id of the medical location where this entity was created. */

    val medicalLocationId: kotlin.String? = null,

    /* Soft delete (unix epoch in ms) timestamp of the object. */

    val endOfLife: kotlin.Long? = null,

    /* hard delete (unix epoch in ms) timestamp of the object. Filled automatically when deletePatient is called. */

    val deletionDate: kotlin.Long? = null,

    /* The logical id of the healthcare element, used to link together different versions of the same healthcare element. We encourage using either a v4 UUID or a HL7 Id. */

    val healthElementId: kotlin.String? = null,

    /* The date (unix epoch in ms) when the healthcare element is noted to have started and also closes on the same date */

    val valueDate: kotlin.Long? = null,

    /* The date (unix epoch in ms) of the start of the healthcare element. */

    val openingDate: kotlin.Long? = null,

    /* The date (unix epoch in ms) marking the end of the healthcare element. */

    val closingDate: kotlin.Long? = null,

    /* Description of the healthcare element. */

    val descr: kotlin.String? = null,

    /* A text note (can be confidential, encrypted by default). */

    val note: kotlin.String? = null,

    /* Id of the opening contact when the healthcare element was created. */

    val idOpeningContact: kotlin.String? = null,

    /* Id of the closing contact for the healthcare element. */

    val idClosingContact: kotlin.String? = null,

    /* Id of the service when a service is used to create a healthcare element. */

    val idService: kotlin.String? = null,

    /* Left or Right dominance/preference. */

    val laterality: HealthElementDto.Laterality? = null,

    /* The base64 encoded data of this object, formatted as JSON and encrypted in AES using the random master key from encryptionKeys. */

    val encryptedSelf: kotlin.String? = null

) {

    /**
     * Left or Right dominance/preference.
     *
     * Values: left,right
     */
    enum class Laterality(val value: kotlin.String) {
        left("left"),
        right("right");
    }
}

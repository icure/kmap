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
 * The details of prescribed or suggested medication
 *
 * @param compoundPrescription
 * @param substanceProduct
 * @param medicinalProduct
 * @param numberOfPackages
 * @param batch
 * @param instructionForPatient
 * @param commentForDelivery
 * @param drugRoute
 * @param temporality
 * @param frequency
 * @param reimbursementReason
 * @param substitutionAllowed
 * @param beginMoment
 * @param endMoment
 * @param deliveryMoment
 * @param endExecutionMoment
 * @param duration
 * @param renewal
 * @param knownUsage
 * @param regimen
 * @param posology
 * @param agreements
 * @param medicationSchemeIdOnSafe
 * @param medicationSchemeSafeVersion
 * @param medicationSchemeTimeStampOnSafe
 * @param medicationSchemeDocumentId
 * @param safeIdName
 * @param idOnSafes
 * @param timestampOnSafe
 * @param changeValidated
 * @param newSafeMedication
 * @param medicationUse
 * @param beginCondition
 * @param endCondition
 * @param origin
 * @param medicationChanged
 * @param posologyChanged
 * @param suspension
 * @param prescriptionRID
 * @param status
 */

data class MedicationDto (


    val compoundPrescription: kotlin.String? = null,


    val substanceProduct: SubstanceproductDto? = null,


    val medicinalProduct: MedicinalproductDto? = null,


    val numberOfPackages: kotlin.Int? = null,


    val batch: kotlin.String? = null,


    val instructionForPatient: kotlin.String? = null,


    val commentForDelivery: kotlin.String? = null,


    val drugRoute: kotlin.String? = null,


    val temporality: kotlin.String? = null,


    val frequency: CodeStubDto? = null,


    val reimbursementReason: CodeStubDto? = null,


    val substitutionAllowed: kotlin.Boolean? = null,


    val beginMoment: kotlin.Long? = null,


    val endMoment: kotlin.Long? = null,


    val deliveryMoment: kotlin.Long? = null,


    val endExecutionMoment: kotlin.Long? = null,


    val duration: DurationDto? = null,


    val renewal: RenewalDto? = null,


    val knownUsage: kotlin.Boolean? = null,


    val regimen: kotlin.collections.List<RegimenItemDto>? = null,


    val posology: kotlin.String? = null,


    val agreements: kotlin.collections.Map<kotlin.String, ParagraphAgreementDto>? = null,


    val medicationSchemeIdOnSafe: kotlin.String? = null,


    val medicationSchemeSafeVersion: kotlin.Int? = null,


    val medicationSchemeTimeStampOnSafe: kotlin.Long? = null,


    val medicationSchemeDocumentId: kotlin.String? = null,


    val safeIdName: kotlin.String? = null,


    val idOnSafes: kotlin.String? = null,


    val timestampOnSafe: kotlin.Long? = null,


    val changeValidated: kotlin.Boolean? = null,


    val newSafeMedication: kotlin.Boolean? = null,


    val medicationUse: kotlin.String? = null,


    val beginCondition: kotlin.String? = null,


    val endCondition: kotlin.String? = null,


    val origin: kotlin.String? = null,


    val medicationChanged: kotlin.Boolean? = null,


    val posologyChanged: kotlin.Boolean? = null,


    val suspension: kotlin.collections.List<SuspensionDto>? = null,


    val prescriptionRID: kotlin.String? = null,


    val status: kotlin.Int? = null

)

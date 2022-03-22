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
 *
 *
 * @param id
 * @param rev
 * @param deletionDate hard delete (unix epoch in ms) timestamp of the object. Filled automatically when deletePatient is called.
 * @param chapterName
 * @param paragraphName
 * @param startDate
 * @param createdTms
 * @param createdUserId
 * @param endDate
 * @param keyStringNl
 * @param keyStringFr
 * @param agreementType
 * @param processType
 * @param legalReference
 * @param publicationDate
 * @param modificationDate
 * @param processTypeOverrule
 * @param paragraphVersion
 * @param agreementTypePro
 * @param modificationStatus
 */

data class ParagraphDto (


    val id: kotlin.String,


    val rev: kotlin.String? = null,

    /* hard delete (unix epoch in ms) timestamp of the object. Filled automatically when deletePatient is called. */

    val deletionDate: kotlin.Long? = null,


    val chapterName: kotlin.String? = null,


    val paragraphName: kotlin.String? = null,


    val startDate: kotlin.Long? = null,


    val createdTms: kotlin.Long? = null,


    val createdUserId: kotlin.String? = null,


    val endDate: kotlin.Long? = null,


    val keyStringNl: kotlin.String? = null,


    val keyStringFr: kotlin.String? = null,


    val agreementType: kotlin.String? = null,


    val processType: kotlin.Long? = null,


    val legalReference: kotlin.String? = null,


    val publicationDate: kotlin.Long? = null,


    val modificationDate: kotlin.Long? = null,


    val processTypeOverrule: kotlin.String? = null,


    val paragraphVersion: kotlin.Long? = null,


    val agreementTypePro: kotlin.String? = null,


    val modificationStatus: kotlin.String? = null

)

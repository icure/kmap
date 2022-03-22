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
 * @param secretForeignKeys
 * @param excludedIds
 * @param tags
 * @param contexts
 * @param recipient
 * @param softwareName
 * @param softwareVersion
 * @param psy
 * @param documentId
 * @param attachmentId
 * @param note
 */

data class DiaryNoteExportInfoDto (


    val secretForeignKeys: kotlin.collections.List<kotlin.String> = emptyList(),


    val excludedIds: kotlin.collections.List<kotlin.String> = emptyList(),


    val tags: kotlin.collections.List<kotlin.String> = emptyList(),


    val contexts: kotlin.collections.List<kotlin.String> = emptyList(),


    val recipient: HealthcarePartyDto? = null,


    val softwareName: kotlin.String? = null,


    val softwareVersion: kotlin.String? = null,


    val psy: kotlin.Boolean? = null,


    val documentId: kotlin.String? = null,


    val attachmentId: kotlin.String? = null,


    val note: kotlin.String? = null

)

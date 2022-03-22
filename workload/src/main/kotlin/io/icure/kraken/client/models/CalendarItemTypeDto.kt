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
 * @param duration
 * @param docIds
 * @param otherInfos
 * @param subjectByLanguage
 * @param rev
 * @param deletionDate hard delete (unix epoch in ms) timestamp of the object. Filled automatically when deletePatient is called.
 * @param name
 * @param color
 * @param externalRef
 * @param mikronoId
 */

data class CalendarItemTypeDto (


    val id: kotlin.String,


    val duration: kotlin.Int = 0,


    val docIds: kotlin.collections.List<kotlin.String> = emptyList(),


    val otherInfos: kotlin.collections.Map<kotlin.String, kotlin.String> = emptyMap(),


    val subjectByLanguage: kotlin.collections.Map<kotlin.String, kotlin.String> = emptyMap(),


    val rev: kotlin.String? = null,

    /* hard delete (unix epoch in ms) timestamp of the object. Filled automatically when deletePatient is called. */

    val deletionDate: kotlin.Long? = null,


    val name: kotlin.String? = null,


    val color: kotlin.String? = null,


    val externalRef: kotlin.String? = null,


    val mikronoId: kotlin.String? = null

)

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
 * @param `value`
 * @param min
 * @param max
 * @param ref
 * @param severity
 * @param severityCode
 * @param evolution
 * @param unit
 * @param unitCodes
 * @param comment
 * @param comparator
 */

data class MeasureDto (


    val `value`: kotlin.Double? = null,


    val min: kotlin.Double? = null,


    val max: kotlin.Double? = null,


    val ref: kotlin.Double? = null,


    val severity: kotlin.Int? = null,


    val severityCode: kotlin.String? = null,


    val evolution: kotlin.Int? = null,


    val unit: kotlin.String? = null,


    val unitCodes: kotlin.collections.Set<CodeStubDto>? = null,


    val comment: kotlin.String? = null,


    val comparator: kotlin.String? = null

)

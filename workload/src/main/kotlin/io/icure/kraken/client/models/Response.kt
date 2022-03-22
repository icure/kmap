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
 * @param status
 * @param headerNames
 * @param characterEncoding
 * @param contentType
 * @param outputStream
 * @param writer
 * @param contentLength
 * @param contentLengthLong
 * @param bufferSize
 * @param committed
 * @param locale
 */

data class Response (


    val status: kotlin.Int? = null,


    val headerNames: kotlin.collections.List<kotlin.String>? = null,


    val characterEncoding: kotlin.String? = null,


    val contentType: kotlin.String? = null,


    val outputStream: RestV2BeSamv2ChapChapterNameParagraphNameVerseSeqAddeddocDocSeqLanguageOutputStream? = null,


    val writer: kotlin.Any? = null,


    val contentLength: kotlin.Int? = null,


    val contentLengthLong: kotlin.Long? = null,


    val bufferSize: kotlin.Int? = null,


    val committed: kotlin.Boolean? = null,


    val locale: RestV2BeSamv2ChapChapterNameParagraphNameVerseSeqAddeddocDocSeqLanguageLocale? = null

)

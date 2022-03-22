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
 * @param source
 * @param target
 * @param owner
 * @param createTarget
 * @param continuous
 * @param docIds
 * @param replicationState
 * @param replicationStateTime
 * @param replicationStats
 * @param errorCount
 * @param revsInfo
 * @param revHistory
 */

data class ReplicatorDocumentDto (


    val id: kotlin.String,


    val rev: kotlin.String? = null,


    val source: RemoteDto? = null,


    val target: RemoteDto? = null,


    val owner: kotlin.String? = null,


    val createTarget: kotlin.Boolean? = null,


    val continuous: kotlin.Boolean? = null,


    val docIds: kotlin.collections.List<kotlin.String>? = null,


    val replicationState: kotlin.String? = null,


    val replicationStateTime: java.time.OffsetDateTime? = null,


    val replicationStats: ReplicationStatsDto? = null,


    val errorCount: kotlin.Int? = null,


    val revsInfo: kotlin.collections.List<kotlin.collections.Map<kotlin.String, kotlin.String>>? = null,


    val revHistory: kotlin.collections.Map<kotlin.String, kotlin.String>? = null

)

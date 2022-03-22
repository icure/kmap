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
 * @param from
 * @param to
 * @param deliveryEnvironment
 * @param code
 * @param codeType
 * @param price
 * @param cheap
 * @param cheapest
 * @param reimbursable
 * @param reimbursements
 * @param productId
 */

data class DmppDto (


    val id: kotlin.String? = null,


    val from: kotlin.Long? = null,


    val to: kotlin.Long? = null,


    val deliveryEnvironment: DmppDto.DeliveryEnvironment? = null,


    val code: kotlin.String? = null,


    val codeType: DmppDto.CodeType? = null,


    val price: kotlin.String? = null,


    val cheap: kotlin.Boolean? = null,


    val cheapest: kotlin.Boolean? = null,


    val reimbursable: kotlin.Boolean? = null,


    val reimbursements: kotlin.collections.List<ReimbursementDto>? = null,


    val productId: kotlin.String? = null

) {

    /**
     *
     *
     * Values: p,a,h,r
     */
    enum class DeliveryEnvironment(val value: kotlin.String) {
        p("P"),
        a("A"),
        h("H"),
        r("R");
    }
    /**
     *
     *
     * Values: cNK,pSEUDO
     */
    enum class CodeType(val value: kotlin.String) {
        cNK("CNK"),
        pSEUDO("PSEUDO");
    }
}

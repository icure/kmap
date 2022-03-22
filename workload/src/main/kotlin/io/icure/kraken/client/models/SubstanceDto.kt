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
 * @param code
 * @param chemicalForm
 * @param name
 * @param note
 * @param standardSubstances
 */

data class SubstanceDto (


    val id: kotlin.String,


    val rev: kotlin.String? = null,

    /* hard delete (unix epoch in ms) timestamp of the object. Filled automatically when deletePatient is called. */

    val deletionDate: kotlin.Long? = null,


    val code: kotlin.String? = null,


    val chemicalForm: kotlin.String? = null,


    val name: SamTextDto? = null,


    val note: SamTextDto? = null,


    val standardSubstances: kotlin.collections.List<StandardSubstanceDto>? = null

)

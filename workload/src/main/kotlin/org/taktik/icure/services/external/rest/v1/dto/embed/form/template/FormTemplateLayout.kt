package org.taktik.icure.services.external.rest.v1.dto.embed.form.template


data class FormTemplateLayout(
    val form: String,
    val sections: List<Section> = emptyList(),
    val description: String? = null,
    val keywords: List<String>? = null,
)

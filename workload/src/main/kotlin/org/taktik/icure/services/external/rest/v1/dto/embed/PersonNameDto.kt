package org.taktik.icure.services.external.rest.v1.dto.embed



data class PersonNameDto(
         val lastName: String? = null,
         val firstNames: List<String> = emptyList(),
         val start: Long? = null,
         val end: Long? = null,
         val prefix: List<String> = emptyList(),
         val suffix: List<String> = emptyList(),
         val text: String? = null,
         val use: PersonNameUseDto? = null
)

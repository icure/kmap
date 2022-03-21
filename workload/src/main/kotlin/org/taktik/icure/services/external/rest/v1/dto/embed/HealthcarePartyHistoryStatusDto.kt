package org.taktik.icure.services.external.rest.v1.dto.embed


data class HealthcarePartyHistoryStatusDto(val status: HealthcarePartyStatusDto, val specialisationCode: String, val startDate: Long, val active: Boolean)

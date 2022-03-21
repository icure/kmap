package org.taktik.icure.entities.embed


data class HealthcarePartyHistoryStatus(val status: HealthcarePartyStatus, val specialisationCode: String, val startDate: Long, val active: Boolean)

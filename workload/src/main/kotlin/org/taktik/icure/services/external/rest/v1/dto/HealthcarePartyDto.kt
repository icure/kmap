/*
 *  iCure Data Stack. Copyright (c) 2020 Taktik SA
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful, but
 *     WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public
 *     License along with this program.  If not, see
 *     <https://www.gnu.org/licenses/>.
 */
package org.taktik.icure.services.external.rest.v1.dto


import org.taktik.icure.services.external.rest.v1.dto.base.CodeStubDto
import org.taktik.icure.services.external.rest.v1.dto.base.CryptoActorDto
import org.taktik.icure.services.external.rest.v1.dto.base.DataOwnerDto
import org.taktik.icure.services.external.rest.v1.dto.base.NamedDto
import org.taktik.icure.services.external.rest.v1.dto.base.PersonDto
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto
import org.taktik.icure.services.external.rest.v1.dto.embed.FinancialInstitutionInformationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.FlatRateTarificationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.GenderDto
import org.taktik.icure.services.external.rest.v1.dto.embed.HealthcarePartyHistoryStatusDto
import org.taktik.icure.services.external.rest.v1.dto.embed.HealthcarePartyStatusDto
import org.taktik.icure.services.external.rest.v1.dto.embed.PersonNameDto
import org.taktik.icure.services.external.rest.v1.dto.embed.TelecomTypeDto

data class HealthcarePartyDto(
         override val id: String,
         override val rev: String? = null,
         val created: Long? = null,
         val modified: Long? = null,
         override val deletionDate: Long? = null,

         override val name: String? = null,
         override val lastName: String? = null,
         override val firstName: String? = null,
         override val names: List<PersonNameDto> = emptyList(),
         override val gender: GenderDto? = null,
         override val civility: String? = null,
         override val companyName: String? = null,
         val speciality: String? = null,
         val bankAccount: String? = null,
         val bic: String? = null,
        val proxyBankAccount: String? = null,
        val proxyBic: String? = null,
         val invoiceHeader: String? = null,
         val cbe: String? = null,
         val ehp: String? = null,
         val userId: String? = null,
         val parentId: String? = null,
        val convention: Int? = null, //0,1,2,9
         val nihii: String? = null, //institution, person
        val nihiiSpecCode: String? = null, //don't show field in the GUI
         val ssin: String? = null,
         override val addresses: List<AddressDto> = emptyList(),
         override val languages: List<String> = emptyList(),
         val picture: ByteArray? = null,
         val statuses: Set<HealthcarePartyStatusDto> = emptySet(),
         val statusHistory: List<HealthcarePartyHistoryStatusDto> = emptyList(),

         val specialityCodes: Set<CodeStubDto> = emptySet(), //Speciality codes, default is first

         val sendFormats: Map<TelecomTypeDto, String> = emptyMap(),
         val notes: String? = null,
         val financialInstitutionInformation: List<FinancialInstitutionInformationDto> = emptyList(),

        // Medical houses
         var billingType: String? = null, // "serviceFee" (à l'acte) or "flatRate" (forfait)
        val type: String? = null, // "persphysician" or "medicalHouse" or "perstechnician"
        val contactPerson: String? = null,
        val contactPersonHcpId: String? = null,
        val supervisorId: String? = null,
        val flatRateTarifications: List<FlatRateTarificationDto> = emptyList(),
        val importedData: Map<String, String> = emptyMap(),

        @Deprecated("Use properties instead")
        val options: Map<String, String> = emptyMap(),
        override val properties: Set<PropertyStubDto> = emptySet(),

        override val hcPartyKeys: Map<String, Array<String>> = emptyMap(),
        override val aesExchangeKeys: Map<String, Map<String, Array<String>>> = emptyMap(),
        override val transferKeys: Map<String, Map<String, String>> = emptyMap(),
        override val lostHcPartyKeys: Set<String> = emptySet(),
        override val privateKeyShamirPartitions: Map<String, String> = emptyMap(), //Format is hcpId of key that has been partitionned : "threshold⎮partition in hex"
        override val publicKey: String? = null
) : StoredDocumentDto, NamedDto, PersonDto, CryptoActorDto, DataOwnerDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

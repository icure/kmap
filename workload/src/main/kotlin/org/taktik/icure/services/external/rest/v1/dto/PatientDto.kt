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
import org.taktik.icure.services.external.rest.v1.dto.base.EncryptableDto
import org.taktik.icure.services.external.rest.v1.dto.base.ICureDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.base.IdentifierDto
import org.taktik.icure.services.external.rest.v1.dto.base.PersonDto
import org.taktik.icure.services.external.rest.v1.dto.base.StoredDocumentDto
import org.taktik.icure.services.external.rest.v1.dto.embed.AddressDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DeactivationReasonDto
import org.taktik.icure.services.external.rest.v1.dto.embed.DelegationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.EmploymentInfoDto
import org.taktik.icure.services.external.rest.v1.dto.embed.FinancialInstitutionInformationDto
import org.taktik.icure.services.external.rest.v1.dto.embed.GenderDto
import org.taktik.icure.services.external.rest.v1.dto.embed.InsurabilityDto
import org.taktik.icure.services.external.rest.v1.dto.embed.MedicalHouseContractDto
import org.taktik.icure.services.external.rest.v1.dto.embed.PartnershipDto
import org.taktik.icure.services.external.rest.v1.dto.embed.PatientHealthCarePartyDto
import org.taktik.icure.services.external.rest.v1.dto.embed.PersonNameDto
import org.taktik.icure.services.external.rest.v1.dto.embed.PersonalStatusDto
import org.taktik.icure.services.external.rest.v1.dto.embed.SchoolingInfoDto

data class PatientDto(
         override val id: String,
         override val rev: String? = null,
        val identifier: List<IdentifierDto> = emptyList(),
        override val created: Long? = null,
        override val modified: Long? = null,
        override val author: String? = null,
        override val responsible: String? = null,
        override val tags: Set<CodeStubDto> = emptySet(),
        override val codes: Set<CodeStubDto> = emptySet(),
        override val endOfLife: Long? = null,
        override val deletionDate: Long? = null,
         override val firstName: String? = null,
         override val lastName: String? = null, //Is usually either maidenName or spouseName,
         override val names: List<PersonNameDto> = emptyList(),
         override val companyName: String? = null,
         override val languages: List<String> = emptyList(), //alpha-2 code http://www.loc.gov/standards/iso639-2/ascii_8bits.html,
         override val addresses: List<AddressDto> = emptyList(),
         override val civility: String? = null,
         override val gender: GenderDto? = GenderDto.unknown,
         val birthSex: GenderDto? = GenderDto.unknown,
         val mergeToPatientId: String? = null,
         val mergedIds: Set<String> = emptySet(),
         val alias: String? = null,
         val active: Boolean = true,
         val deactivationReason: DeactivationReasonDto = DeactivationReasonDto.none,
         val ssin: String? = null,
         val maidenName: String? = null,// Never changes (nom de jeune fille),
         val spouseName: String? = null, // Name of the spouse after marriage,
         val partnerName: String? = null, // Name of the partner, sometimes equal to spouseName,
         val personalStatus: PersonalStatusDto? = PersonalStatusDto.unknown,
         val dateOfBirth: Int? = null, // YYYYMMDD if unknown, 00, ex:20010000 or,
         val dateOfDeath: Int? = null, // YYYYMMDD if unknown, 00, ex:20010000 or,
         val timestampOfLatestEidReading: Long? = null,
         val placeOfBirth: String? = null,
         val placeOfDeath: String? = null,
         val deceased: Boolean? = null,
         val education: String? = null,
         val profession: String? = null,
         val note: String? = null,
         val administrativeNote: String? = null,
         val nationality: String? = null,
         val race: String? = null,
         val ethnicity: String? = null,
         val preferredUserId: String? = null,
         val picture: ByteArray? = null,
         val externalId: String? = null, //No guarantee of unicity
         val insurabilities: List<InsurabilityDto> = emptyList(),
         val partnerships: List<PartnershipDto> = emptyList(),
         val patientHealthCareParties: List<PatientHealthCarePartyDto> = emptyList(),
         val financialInstitutionInformation: List<FinancialInstitutionInformationDto> = emptyList(),
         val medicalHouseContracts: List<MedicalHouseContractDto> = emptyList(),
          val patientProfessions: List<CodeStubDto> = emptyList(),
         val parameters: Map<String, List<String>> = emptyMap(),
         val properties: Set<PropertyStubDto> = emptySet(),

        override val hcPartyKeys: Map<String, Array<String>> = emptyMap(),
        override val aesExchangeKeys: Map<String, Map<String, Array<String>>> = emptyMap(),
        override val transferKeys: Map<String, Map<String, String>> = emptyMap(),
        override val lostHcPartyKeys: Set<String> = emptySet(),
        override val privateKeyShamirPartitions: Map<String, String> = emptyMap(),
        override val publicKey: String? = null,

        override val secretForeignKeys: Set<String> = emptySet(),
        override val cryptedForeignKeys: Map<String, Set<DelegationDto>> = emptyMap(),
        override val delegations: Map<String, Set<DelegationDto>> = emptyMap(),
        override val encryptionKeys: Map<String, Set<DelegationDto>> = emptyMap(),
        override val encryptedSelf: String? = null,

        override val medicalLocationId: String? = null,

        @get:Deprecated("Do not use") val nonDuplicateIds: Set<String> = emptySet(),
        @get:Deprecated("Do not use") val encryptedAdministrativesDocuments: Set<String> = emptySet(),
        @get:Deprecated("Use note or administrativeNote") val comment: String? = null,
        @get:Deprecated("Use note or administrativeNote") val warning: String? = null,
        @get:Deprecated("Use properties instead") val fatherBirthCountry: CodeStubDto? = null, //Deprecated won't work on $ref, because the serialisation gets rid of everything that is not $ref (in conformance with the spec)
        @get:Deprecated("Use properties instead") val birthCountry: CodeStubDto? = null, //Deprecated won't work on $ref, because the serialisation gets rid of everything that is not $ref (in conformance with the spec)
        @get:Deprecated("Use properties instead") val nativeCountry: CodeStubDto? = null, //Deprecated won't work on $ref, because the serialisation gets rid of everything that is not $ref (in conformance with the spec)
        @get:Deprecated("Use properties instead") val socialStatus: CodeStubDto? = null, //Deprecated won't work on $ref, because the serialisation gets rid of everything that is not $ref (in conformance with the spec)
        @get:Deprecated("Use properties instead") val mainSourceOfIncome: CodeStubDto? = null, //Deprecated won't work on $ref, because the serialisation gets rid of everything that is not $ref (in conformance with the spec)
        @get:Deprecated("Use properties instead") val schoolingInfos: List<SchoolingInfoDto> = emptyList(),
        @get:Deprecated("Use properties instead") val employementInfos: List<EmploymentInfoDto> = emptyList(),

        ) : StoredDocumentDto, ICureDocumentDto<String>, PersonDto, EncryptableDto, CryptoActorDto {
    override fun withIdRev(id: String?, rev: String) = if (id != null) this.copy(id = id, rev = rev) else this.copy(rev = rev)
    override fun withDeletionDate(deletionDate: Long?) = this.copy(deletionDate = deletionDate)
}

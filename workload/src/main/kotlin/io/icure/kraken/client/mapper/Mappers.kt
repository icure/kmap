package io.icure.kraken.client.mapper

import io.icure.kraken.client.models.decrypted.*

import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper
interface AccessLogMapper {
    fun map(accessLog: AccessLogDto): io.icure.kraken.client.models.AccessLogDto
    fun map(accessLog: io.icure.kraken.client.models.AccessLogDto): AccessLogDto
}

@Mapper
interface ArticleMapper {
    fun map(article: ArticleDto): io.icure.kraken.client.models.ArticleDto
    fun map(article: io.icure.kraken.client.models.ArticleDto): ArticleDto
}

@Mapper
interface ClassificationMapper {
    fun map(classification: ClassificationDto): io.icure.kraken.client.models.ClassificationDto
    fun map(classification: io.icure.kraken.client.models.ClassificationDto): ClassificationDto
}

@Mapper(uses = [ServiceMapper::class])
interface ContactMapper {
    fun map(contact: ContactDto): io.icure.kraken.client.models.ContactDto
    fun map(contact: io.icure.kraken.client.models.ContactDto): ContactDto
}

@Mapper
interface ServiceMapper {

    fun map(service: ServiceDto): io.icure.kraken.client.models.ServiceDto
    fun map(service: io.icure.kraken.client.models.ServiceDto): ServiceDto

    fun map(content: ContentDto): io.icure.kraken.client.models.ContentDto
    fun map(content: io.icure.kraken.client.models.ContentDto): ContentDto

    fun mapQualifiedLinks(qualifiedLinks: Map<ServiceDto.LinkQualification, Map<String, String>>): Map<String, Map<String, String>> {
        return qualifiedLinks
            .map { (key, value) -> key.value to value }
            .toMap()
    }

    fun mapRawQualifiedLinks(qualifiedLinks: Map<String, Map<String, String>>): Map<ServiceDto.LinkQualification, Map<String, String>> {
        return qualifiedLinks
            .map { (key, value) -> ServiceDto.LinkQualification.valueOf(key) to value }
            .toMap()
    }
}

@Mapper
interface DocumentMapper {
    fun map(document: DocumentDto): io.icure.kraken.client.models.DocumentDto
    fun map(document: io.icure.kraken.client.models.DocumentDto): DocumentDto
}

@Mapper
interface FormMapper {
    fun map(form: FormDto): io.icure.kraken.client.models.FormDto
    fun map(form: io.icure.kraken.client.models.FormDto): FormDto
}

@Mapper
interface HealthElementMapper {
    fun map(healthElement: HealthElementDto): io.icure.kraken.client.models.HealthElementDto
    fun map(healthElement: io.icure.kraken.client.models.HealthElementDto): HealthElementDto
}

@Mapper
interface InvoiceMapper {
    fun map(invoice: InvoiceDto): io.icure.kraken.client.models.InvoiceDto
    fun map(invoice: io.icure.kraken.client.models.InvoiceDto): InvoiceDto
}

@Mapper
interface MessageMapper {
    fun map(message: MessageDto): io.icure.kraken.client.models.MessageDto
    fun map(message: io.icure.kraken.client.models.MessageDto): MessageDto
}

@Mapper
interface PatientMapper {
    fun map(patient: PatientDto): io.icure.kraken.client.models.PatientDto
    fun map(patient: io.icure.kraken.client.models.PatientDto): PatientDto
}

@Mapper
interface ReceiptMapper {
    fun map(receipt: ReceiptDto): io.icure.kraken.client.models.ReceiptDto
    fun map(receipt: io.icure.kraken.client.models.ReceiptDto): ReceiptDto
}

@Mapper
interface TimeTableMapper {
    fun map(timeTable: TimeTableDto): io.icure.kraken.client.models.TimeTableDto
    fun map(timeTable: io.icure.kraken.client.models.TimeTableDto): TimeTableDto
}


package org.taktik.icure.services.external.rest.v1.dto.embed.form.template

import org.taktik.icure.handlers.JsonDiscriminated
import org.taktik.icure.handlers.JsonPolymorphismRoot
import org.taktik.icure.services.external.rest.v1.handlers.JacksonStructureElementDeserializer


interface StructureElement

@JsonPolymorphismRoot(Field::class)

@JsonDiscriminated("textfield")
class TextField(
        field: String,
        shortLabel: String? = null,
        rows: Int? = null,
        grows: Boolean? = null,
        schema: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.textfield, shortLabel, rows, null, grows, schema, tags, codifications, options)

@JsonPolymorphismRoot(Field::class)

@JsonDiscriminated("measure-field")
class MeasureField(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`measure-field`, shortLabel, null, null, null, null, tags, codifications, options)

@JsonPolymorphismRoot(Field::class)

@JsonDiscriminated("number-field")
class NumberField(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`number-field`, shortLabel, null, null, null, null, tags, codifications, options)

@JsonPolymorphismRoot(Field::class)

@JsonDiscriminated("date-picker")
class DatePicker(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`date-picker`, shortLabel, null, null, null, null, tags, codifications, options)

@JsonPolymorphismRoot(Field::class)

@JsonDiscriminated("time-picker")
class TimePicker(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`time-picker`, shortLabel, null, null, null, null, tags, codifications, options)

@JsonPolymorphismRoot(Field::class)

@JsonDiscriminated("date-time-picker")
class DateTimePicker(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`date-time-picker`, shortLabel, null, null, null, null, tags, codifications, options)

@JsonPolymorphismRoot(Field::class)

@JsonDiscriminated("multiple-choice")
class MultipleChoice(
        field: String,
        shortLabel: String? = null,
        tags: List<String>? = null,
        codifications: List<String>? = null,
        options: Map<String, Any>? = null,
) : Field(field, FieldType.`multiple-choice`, shortLabel, null, null, null, null, tags, codifications, options)


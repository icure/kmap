/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.entities.samv2.stub

import org.taktik.icure.entities.samv2.embed.SamText
import org.taktik.icure.entities.samv2.embed.StandardSubstance
import java.io.Serializable

data class SubstanceStub(
        val id: String? = null,
        val code: String? = null,
        val chemicalForm: String? = null,
        val name: SamText? = null,
        val note: SamText? = null,
        val standardSubstances: Set<StandardSubstance>? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SubstanceStub) return false

        if (code != other.code) return false
        if (chemicalForm != other.chemicalForm) return false
        if (name != other.name) return false
        if (note != other.note) return false
        if (standardSubstances != other.standardSubstances) return false

        return true
    }

    override fun hashCode(): Int {
        var result = code?.hashCode() ?: 0
        result = 31 * result + (chemicalForm?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (note?.hashCode() ?: 0)
        result = 31 * result + (standardSubstances?.hashCode() ?: 0)
        return result
    }
}


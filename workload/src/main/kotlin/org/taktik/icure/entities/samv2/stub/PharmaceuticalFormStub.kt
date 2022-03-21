/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.entities.samv2.stub

import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.entities.samv2.embed.SamText
import java.io.Serializable

data class PharmaceuticalFormStub(
       val id: String? = null,
        val code: String? = null,
        val name: SamText? = null,
        val standardForms: Set<CodeStub> = emptySet(),
) : Serializable

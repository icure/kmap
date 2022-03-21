package org.taktik.icure.entities.security

import org.taktik.icure.domain.filter.impl.predicate.AlwaysPredicate

data class AlwaysPermissionItem(override val type:PermissionType):PermissionItem {
    override val predicate = AlwaysPredicate()
    override fun merge(other: PermissionItem) = this
}

package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.Version
import org.springframework.data.jpa.repository.JpaRepository

interface VersionRepository : JpaRepository<Version, Long> {

    fun findAllByIdIn(ids: Collection<Long>): List<Version>
}

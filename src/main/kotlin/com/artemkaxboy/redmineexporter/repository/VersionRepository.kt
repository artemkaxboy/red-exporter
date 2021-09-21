package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.Version
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

const val STATUS_OPENED = "open"

interface VersionRepositoryI : JpaRepository<Version, Long> {

    fun findByProjectIdInAndStatus(projectId: List<Long>, status: String): List<Version>
}

@Repository
class VersionRepository(private val versionRepositoryI: VersionRepositoryI) :
    VersionRepositoryI by versionRepositoryI {

    fun findByProjectIdInAndStatusIsOpened(projectId: List<Long>): List<Version> {
        return findByProjectIdInAndStatus(projectId, STATUS_OPENED)
    }
}

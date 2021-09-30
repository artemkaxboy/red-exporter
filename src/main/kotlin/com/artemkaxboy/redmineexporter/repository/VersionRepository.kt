package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.Version
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

const val STATUS_OPENED = "open"

interface VersionRepositoryI : JpaRepository<Version, Long> {

    @Query("SELECT v FROM Version v JOIN FETCH v.project WHERE v.projectId IN :projectId AND v.status = :status")
    fun findByProjectIdInAndStatus(projectId: List<Long>, status: String): List<Version>
}

@Repository
class VersionRepository(private val versionRepositoryI: VersionRepositoryI) :
    VersionRepositoryI by versionRepositoryI {

    @Suppress("SpringDataMethodInconsistencyInspection") // IsOpened suffix cannot be correctly interpreted
    fun findByProjectIdInAndStatusIsOpened(projectId: List<Long>): List<Version> {
        return findByProjectIdInAndStatus(projectId, STATUS_OPENED)
    }
}

package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.Version
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

const val STATUS_OPENED = "open"

interface VersionRepositoryI : JpaRepository<Version, Long> {

    @Query("SELECT v FROM Version v JOIN FETCH v.project WHERE v.projectId IN :projectId AND v.status = :status")
    fun fetchByProjectIdInAndStatus(projectId: List<Long>, status: String): List<Version>
}

@Repository
class VersionRepository(private val versionRepositoryI: VersionRepositoryI) :
    VersionRepositoryI by versionRepositoryI {

    fun fetchByProjectIdInAndStatusIsOpened(projectId: List<Long>): List<Version> {
        return fetchByProjectIdInAndStatus(projectId, STATUS_OPENED)
    }
}

package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.config.properties.RedmineProperties
import com.artemkaxboy.redmineexporter.entity.IssueStatus
import com.artemkaxboy.redmineexporter.entity.Version
import com.artemkaxboy.redmineexporter.repository.IssueStatusRepository
import com.artemkaxboy.redmineexporter.repository.VersionRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class VersionService(

    private val redmineProperties: RedmineProperties,
    private val versionRepository: VersionRepository,
) {

    fun getAll(): List<Version> {

        return versionRepository.findByProjectIdInAndStatusIsOpened(redmineProperties.projects)
    }
}

package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.config.properties.RedmineProperties
import com.artemkaxboy.redmineexporter.entity.Version
import com.artemkaxboy.redmineexporter.repository.VersionRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class VersionService(

    private val redmineProperties: RedmineProperties,
    private val versionRepository: VersionRepository,
) {

    @Cacheable("versions_catalog")
    fun getVersion(versionId: Long): Version? {
        return versionRepository.findByIdOrNull(versionId)
    }

    fun getVersionByProject(projectId: Long): List<>
}

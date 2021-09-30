package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.config.properties.RedmineProperties
import com.artemkaxboy.redmineexporter.entity.Version
import com.artemkaxboy.redmineexporter.metrics.VersionClosedEventListener
import com.artemkaxboy.redmineexporter.repository.VersionRepository
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service


private val logger = KotlinLogging.logger {}

@Service
class VersionService(

    private val redmineProperties: RedmineProperties,
    private val versionRepository: VersionRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,

    ) {

    private var versions = emptyMap<Long, List<Version>>()

    fun updateVersions() {

        loadVersionsForProject(redmineProperties.projects)
    }

    fun getVersionList(): List<Version> = versions.flatMap { it.value }

    private fun loadVersionsForProject(projectId: List<Long>) {

        val versionsByProject = versionRepository.fetchByProjectIdInAndStatusIsOpened(projectId)
            .groupBy { it.projectId }

        versionsByProject.forEach { (projectId, loadedVersions) ->

            val existingVersions = versions.getOrDefault(projectId, emptyList())

            (existingVersions - loadedVersions)
                .sortedBy { it.id } // for better logs reading
                .forEach { closedVersion ->

                    logger.info {
                        "Version closed: " +
                                "project (#${closedVersion.projectId} ${closedVersion.project?.name}) " +
                                "version (#${closedVersion.id} ${closedVersion.name})"
                    }
                    applicationEventPublisher.publishEvent(VersionClosedEventListener.Event(version = closedVersion))
                }

            (loadedVersions - existingVersions)
                .sortedBy { it.id } // for better logs reading
                .forEach { openedVersion ->
                    logger.info {
                        "Version opened: " +
                                "project (#${openedVersion.projectId} ${openedVersion.project?.name}) " +
                                "version (#${openedVersion.id} ${openedVersion.name})"
                    }
                }
        }

        versions = versionsByProject
    }
}

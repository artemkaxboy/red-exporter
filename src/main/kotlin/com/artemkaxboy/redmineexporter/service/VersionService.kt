package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.config.properties.RedmineProperties
import com.artemkaxboy.redmineexporter.entity.Version
import com.artemkaxboy.redmineexporter.metrics.VersionClosedEventListener
import com.artemkaxboy.redmineexporter.metrics.VersionOpenedEventListener
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

    private var openedVersionsByProject = emptyMap<Long, List<Version>>()

    /**
     * Fetches all opened versions for all project ids listed in properties.
     */
    fun fetchVersions() {

        fetchVersionsForProjects(redmineProperties.projects)
    }

    /**
     * Returns all versions for all projects.
     */
    fun getAllVersions(): List<Version> = openedVersionsByProject.flatMap { projectVersions -> projectVersions.value }

    private fun fetchVersionsForProjects(projectIds: List<Long>) {

        val versionsByProject = versionRepository.findByProjectIdInAndStatusIsOpened(projectIds)
            .groupBy { it.projectId }

        versionsByProject.forEach { (projectId, loadedVersions) ->

            val existingVersions = openedVersionsByProject.getOrDefault(projectId, emptyList())
            notifyChanges(existingVersions, loadedVersions)
        }

        openedVersionsByProject = versionsByProject
    }

    private fun notifyChanges(existingVersions: List<Version>, loadedVersions: List<Version>) {
        notifyClosedVersion(existingVersions, loadedVersions)
        notifyOpenedVersion(existingVersions, loadedVersions)
    }

    private fun notifyClosedVersion(existingVersions: List<Version>, loadedVersions: List<Version>) {
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
    }

    private fun notifyOpenedVersion(existingVersions: List<Version>, loadedVersions: List<Version>) {
        (loadedVersions - existingVersions)
            .sortedBy { it.id } // for better logs reading
            .forEach { openedVersion ->

                logger.info {
                    "Version opened: " +
                            "project (#${openedVersion.projectId} ${openedVersion.project?.name}) " +
                            "version (#${openedVersion.id} ${openedVersion.name})"
                }
                applicationEventPublisher.publishEvent(VersionOpenedEventListener.Event(version = openedVersion))
            }
    }
}

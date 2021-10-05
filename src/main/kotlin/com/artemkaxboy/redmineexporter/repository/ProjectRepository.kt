package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.Project
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Repository of [Project] entities.
 */
interface ProjectRepository: JpaRepository<Project, Long>

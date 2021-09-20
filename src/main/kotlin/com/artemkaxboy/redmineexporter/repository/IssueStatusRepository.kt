package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.IssueStatus
import org.springframework.data.jpa.repository.JpaRepository

interface IssueStatusRepository : JpaRepository<IssueStatus, Long>

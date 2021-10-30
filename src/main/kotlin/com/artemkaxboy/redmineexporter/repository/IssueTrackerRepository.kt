package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.IssueTracker
import org.springframework.data.jpa.repository.JpaRepository

interface IssueTrackerRepository : JpaRepository<IssueTracker, Long>

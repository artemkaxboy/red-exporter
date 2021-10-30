package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.IssueCategory
import org.springframework.data.jpa.repository.JpaRepository

interface IssueCategoryRepository : JpaRepository<IssueCategory, Long>

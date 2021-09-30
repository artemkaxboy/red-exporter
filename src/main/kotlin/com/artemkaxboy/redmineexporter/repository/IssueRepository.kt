package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.Issue
import com.artemkaxboy.redmineexporter.entity.StatusWithMetric
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


@Suppress("SpringDataRepositoryMethodReturnTypeInspection")
interface IssueRepository : JpaRepository<Issue, Long> {

    @Query("SELECT new com.artemkaxboy.redmineexporter.entity.StatusWithMetric(i.statusId, COUNT(i.id)) FROM Issue i WHERE i.fixedVersionId = :fixedVersionId GROUP BY i.statusId")
    fun countByFixedVersionIdGroupedByStatus(fixedVersionId: Long): List<StatusWithMetric>
}

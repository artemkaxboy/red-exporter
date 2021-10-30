package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.Issue
import com.artemkaxboy.redmineexporter.entity.ProjectIssuesMetricByPriorityId
import com.artemkaxboy.redmineexporter.entity.ProjectIssuesMetricByStatusId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


@Suppress("SpringDataRepositoryMethodReturnTypeInspection")
interface IssueRepository : JpaRepository<Issue, Long> {

    /**
     * Returns list
     */
    @Query("SELECT new com.artemkaxboy.redmineexporter.entity.ProjectIssuesMetricByStatusId(i.statusId, COUNT(i.id)) FROM Issue i WHERE i.fixedVersionId = :fixedVersionId GROUP BY i.statusId")
    fun sumByFixedVersionIdGroupedByIssueStatus(fixedVersionId: Long): List<ProjectIssuesMetricByStatusId>

    @Query("SELECT new com.artemkaxboy.redmineexporter.entity.ProjectIssuesMetricByPriorityId(i.priorityId, COUNT(i.id)) FROM Issue i WHERE i.fixedVersionId = :fixedVersionId GROUP BY i.priorityId")
    fun sumByFixedVersionIdGroupedByPriority(fixedVersionId: Long): List<ProjectIssuesMetricByPriorityId>
}

package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query


@Suppress("SpringDataRepositoryMethodReturnTypeInspection")
interface IssueRepository : JpaRepository<Issue, Long> {

    /**
     * Returns list
     */
    @Query("SELECT new com.artemkaxboy.redmineexporter.entity.ProjectIssuesMetricByStatusId(i.statusId, COUNT(i.id)) FROM Issue i WHERE i.fixedVersionId = :fixedVersionId GROUP BY i.statusId")
    fun sumByFixedVersionIdGroupedByIssueStatus(fixedVersionId: Long): List<ProjectIssuesMetricByStatusId>

    @Query("SELECT new com.artemkaxboy.redmineexporter.entity.ProjectIssuesMetricByPriorityId(i.priorityId, s.isClosed, COUNT(i.id)) FROM Issue i INNER JOIN IssueStatus s ON i.statusId = s.id WHERE i.fixedVersionId = :fixedVersionId GROUP BY i.priorityId, s.isClosed")
    fun sumByFixedVersionIdGroupedByPriorityAndIsClosed(fixedVersionId: Long): List<ProjectIssuesMetricByPriorityId>

    @Query("SELECT new com.artemkaxboy.redmineexporter.entity.ProjectIssuesMetricByCategoryId(i.categoryId, s.isClosed, COUNT(i.id)) FROM Issue i INNER JOIN IssueStatus s ON i.statusId = s.id WHERE i.fixedVersionId = :fixedVersionId GROUP BY i.categoryId, s.isClosed")
    fun sumByFixedVersionIdGroupedByCategoryAndIsClosed(fixedVersionId: Long): List<ProjectIssuesMetricByCategoryId>

    @Query("SELECT new com.artemkaxboy.redmineexporter.entity.ProjectIssuesMetricByTrackerId(i.trackerId, s.isClosed, COUNT(i.id)) FROM Issue i INNER JOIN IssueStatus s ON i.statusId = s.id WHERE i.fixedVersionId = :fixedVersionId GROUP BY i.trackerId, s.isClosed")
    fun sumByFixedVersionIdGroupedByTrackerAndIsClosed(fixedVersionId: Long): List<ProjectIssuesMetricByTrackerId>
}

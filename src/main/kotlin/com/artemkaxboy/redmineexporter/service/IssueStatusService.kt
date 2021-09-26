package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.IssueStatus
import com.artemkaxboy.redmineexporter.entity.UNKNOWN_ISSUE_STATUS
import com.artemkaxboy.redmineexporter.repository.IssueStatusRepository
import org.springframework.stereotype.Service

@Service
class IssueStatusService(

    private val issueStatusRepository: IssueStatusRepository,
) {

    private var statuses = emptyMap<Long, IssueStatus>()

    fun updateStatuses() {
        statuses = issueStatusRepository.findAll().associateBy { it.id }
    }

    fun getAllStatuses(): Map<Long, IssueStatus> = statuses

    fun getStatus(statusId: Long): IssueStatus {
        return statuses.getOrDefault(statusId, UNKNOWN_ISSUE_STATUS)
    }
}

package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.IssueStatus
import com.artemkaxboy.redmineexporter.repository.IssueStatusRepository
import org.springframework.stereotype.Service

@Service
class IssueStatusService(

    private val issueStatusRepository: IssueStatusRepository,
) {

    private var statuses = emptyList<IssueStatus>()

    fun updateStatuses() {
        statuses = issueStatusRepository.findAll()
    }

    fun getAllStatuses(): List<IssueStatus> = statuses
}

package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.IssueStatus
import com.artemkaxboy.redmineexporter.repository.IssueStatusRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class IssueStatusService(

    private val issueStatusRepository: IssueStatusRepository
) {

    var statuses = emptyList<IssueStatus>()

    fun getAll(): List<IssueStatus> {
        if (statuses.isEmpty()) {
            loadFromRepository()
        }

        return statuses
    }

    fun loadFromRepository() {
        statuses = issueStatusRepository.findAll()
    }
}

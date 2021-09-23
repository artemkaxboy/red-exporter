package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.IssueStatus
import com.artemkaxboy.redmineexporter.repository.IssueStatusRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class IssueStatusService(

    private val issueStatusRepository: IssueStatusRepository,
) {

    @Cacheable("catalog")
    fun getStatus(statusId: Long): IssueStatus {
        return issueStatusRepository.getById(statusId)
    }
}

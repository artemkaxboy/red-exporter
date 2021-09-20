package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.entity.IssueStatus
import com.artemkaxboy.redmineexporter.repository.IssueStatusRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class IssueStatusService(

    private val issueStatusRepository: IssueStatusRepository
) {

    fun getAll(): List<IssueStatus> {
        return issueStatusRepository.findAll(Sort.by(IssueStatus::position.name))
    }
}

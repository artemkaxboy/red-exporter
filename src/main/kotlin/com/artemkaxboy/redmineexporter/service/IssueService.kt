package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.repository.IssueRepository
import org.springframework.stereotype.Service

@Service
class IssueService(

    private val issueRepository: IssueRepository
) {

    fun countByStatusId(versionId: Long, statusId: Long): Int {
        return issueRepository.countByFixedVersionIdAndStatusId(versionId, statusId)
    }

    fun countByClosed(versionId: Long, closed: Boolean): Int {
        return issueRepository.countClosedByFixedVersionId(versionId, if (closed) 1 else 0)
    }
}

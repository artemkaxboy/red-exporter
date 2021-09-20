package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.Issue
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

interface IssueRepositoryI : JpaRepository<Issue, Long> {

    fun countByFixedVersionIdAndStatusId(fixedVersionId: Long, statusId: Long): Int

    @Query("SELECT COUNT(i.id) FROM Issue i INNER JOIN IssueStatus ist ON i.statusId = ist.id WHERE i.fixedVersionId = :fixedVersionId AND ist.isClosed = :isClosed")
    fun countClosedByFixedVersionId(fixedVersionId: Long, isClosed: Int): Int
}

@Repository
class IssueRepository(issueRepositoryI: IssueRepositoryI) : IssueRepositoryI by issueRepositoryI

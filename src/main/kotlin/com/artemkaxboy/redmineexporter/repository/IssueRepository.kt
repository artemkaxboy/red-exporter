package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.Issue
import com.artemkaxboy.redmineexporter.entity.Issue_
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager

interface IssueRepositoryI : JpaRepository<Issue, Long> {

    fun countByFixedVersionIdAndStatusId(fixedVersionId: Long, statusId: Long): Int

    @Query("SELECT COUNT(i.id) FROM Issue i INNER JOIN IssueStatus ist ON i.statusId = ist.id WHERE i.fixedVersionId = :fixedVersionId AND ist.isClosed = :isClosed")
    fun countClosedByFixedVersionId(fixedVersionId: Long, isClosed: Int): Int
}

@Repository
class IssueRepository(
    private val entityManager: EntityManager,
    issueRepositoryI: IssueRepositoryI
) : IssueRepositoryI by issueRepositoryI {

    // SELECT status_id, COUNT(i.id) FROM issues i WHERE fixed_version_id = '333' GROUP BY status_id
    // https://codeburst.io/criteria-queries-and-jpa-metamodel-with-spring-boot-and-kotlin-9c82be54d626
    // https://www.tabnine.com/code/java/methods/javax.persistence.criteria.CriteriaQuery/groupBy
    fun countByFixedVersionIdGroupedByStatus(fixedVersionId: Long): Map<Long, Long> {
        val builder = entityManager.criteriaBuilder
        val query = builder.createQuery(Pair::class.java)

        val root = query.from(Issue::class.java)

        query.multiselect(root.get(Issue_.statusId), builder.count(root))
        query.where(builder.equal(root.get(Issue_.fixedVersionId), fixedVersionId))
        query.groupBy(root.get(Issue_.statusId))

        return entityManager.createQuery(query).resultList.associate { it.first as Long to it.second as Long }
    }
}

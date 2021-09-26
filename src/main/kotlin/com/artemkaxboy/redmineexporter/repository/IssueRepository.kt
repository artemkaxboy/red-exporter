package com.artemkaxboy.redmineexporter.repository

import com.artemkaxboy.redmineexporter.entity.Issue
import com.artemkaxboy.redmineexporter.entity.Issue_
import com.artemkaxboy.redmineexporter.entity.StatusMetrics
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager


interface IssueRepositoryI : JpaRepository<Issue, Long>

@Repository
class IssueRepository(
    private val entityManager: EntityManager,
    issueRepositoryI: IssueRepositoryI
) : IssueRepositoryI by issueRepositoryI {

    // SELECT status_id, COUNT(i.id) FROM issues i WHERE fixed_version_id = '333' GROUP BY status_id
    // https://codeburst.io/criteria-queries-and-jpa-metamodel-with-spring-boot-and-kotlin-9c82be54d626
    // https://www.tabnine.com/code/java/methods/javax.persistence.criteria.CriteriaQuery/groupBy
    //
    // Attempt to get Pair<Version,Long> have failed, because mysql doesn't allow combine
    // aggregated fields with non-aggregated ones. So, it should be done on upper level.
    @Suppress("SpringDataRepositoryMethodReturnTypeInspection", "SpringDataMethodInconsistencyInspection")
    fun countByFixedVersionIdGroupedByStatus(fixedVersionId: Long): List<StatusMetrics> {
        val builder = entityManager.criteriaBuilder
        val query = builder.createQuery(StatusMetrics::class.java)

        val root = query.from(Issue::class.java)

        query.multiselect(root.get(Issue_.statusId), builder.count(root))
        query.where(builder.equal(root.get(Issue_.fixedVersionId), fixedVersionId))
        query.groupBy(root.get(Issue_.statusId))
        return entityManager.createQuery(query).resultList
    }
}

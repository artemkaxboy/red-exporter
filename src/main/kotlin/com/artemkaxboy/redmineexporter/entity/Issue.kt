package com.artemkaxboy.redmineexporter.entity

import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name = "issues")
class Issue(

    @Id
    val id: Long = -1,

    @Column(name = "tracker_id")
    val trackerId: Long,

    @Column(name = "project_id")
    val projectId: Long,

    @Column(name = "status_id")
    val statusId: Long,

    @Column(name = "priority_id")
    val priorityId: Long,

    @Column(name = "fixed_version_id")
    val fixedVersionId: Long?,

    @Column(name = "author_id")
    val authorId: Long,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Issue

        return id == other.id
    }

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , trackerId = $trackerId , projectId = $projectId , statusId = $statusId , priorityId = $priorityId , fixedVersionId = $fixedVersionId , authorId = $authorId )"
    }
}

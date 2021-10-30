package com.artemkaxboy.redmineexporter.entity

import org.hibernate.Hibernate
import org.jetbrains.annotations.TestOnly
import javax.persistence.*

@Entity
@Table(name = "issue_categories")
class IssueCategory(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    @Column(name = "project_id")
    val projectId: Long,

    val name: String,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as IssueCategory

        return id == other.id
    }

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , projectId = $projectId , name = $name )"
    }

    companion object {

        /**
         * Makes entity with fake defaults.
         */
        @JvmOverloads
        fun make(
            id: Long = -1,
            projectId: Long = -1,
            name: String = "Issue Tracker",
        ) = IssueCategory(
            id = id,
            projectId = projectId,
            name = name,
        )

        val emptyCategory = make(name = "")
    }
}

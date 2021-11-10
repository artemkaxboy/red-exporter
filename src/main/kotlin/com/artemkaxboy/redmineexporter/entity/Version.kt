package com.artemkaxboy.redmineexporter.entity

import com.artemkaxboy.redmineexporter.repository.STATUS_OPENED
import org.hibernate.Hibernate
import org.jetbrains.annotations.TestOnly
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "versions")
class Version(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = -1,

    val name: String,

    @Column(name = "project_id")
    val projectId: Long,

    @ManyToOne
    @JoinColumn(nullable = false, updatable = false, insertable = false)
    val project: Project?,

    val status: String,

    @Column(name = "effective_date")
    val effectiveDate: LocalDate?,
) {

    val effectiveDateString: String
        get() = effectiveDate?.toString() ?: ""

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as Version

        return id == other.id
    }

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , name = $name , projectId = $projectId , status = $status, effectiveDate = $effectiveDate )"
    }

    companion object {

        /**
         * Makes entity with fake defaults.
         */
        @TestOnly
        @JvmOverloads
        fun make(
            id: Long = -1,
            name: String = "Main Project",
            projectId: Long = 1,
            project: Project? = null,
            status: String = STATUS_OPENED,
            effectiveDate: LocalDate = LocalDate.now(),
        ) = Version(
            id = id,
            name = name,
            projectId = projectId,
            project = project,
            status = status,
            effectiveDate = effectiveDate,
        )
    }
}

package com.artemkaxboy.redmineexporter.entity

import org.hibernate.Hibernate
import javax.persistence.*

@Entity
@Table(name = "users")
class User(

    @Id
    val id: Long = -1,

    val login: String,

    private val firstname: String?,
    private val lastname: String?,
) {

    val name: String
        get() = listOfNotNull(firstname, lastname).joinToString(" ")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as User

        return id == other.id
    }

    override fun hashCode(): Int = 0

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id , login = $login )"
    }
}

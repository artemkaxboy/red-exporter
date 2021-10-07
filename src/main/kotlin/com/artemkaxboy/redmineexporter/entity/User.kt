package com.artemkaxboy.redmineexporter.entity

import org.hibernate.Hibernate
import org.jetbrains.annotations.TestOnly
import javax.persistence.*

@Entity
@Table(name = "users")
class User(

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
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
        return this::class.simpleName + "(id = $id , login = $login , firstname = $firstname , lastname = $lastname )"
    }

    companion object {

        /**
         * Makes entity with fake defaults.
         */
        @TestOnly
        @JvmOverloads
        fun make(
            id: Long = 1,
            login: String = "login",
            firstname: String? = "John",
            lastname: String? = "Smith",
        ) =
            User(id = id, login = login, firstname = firstname, lastname = lastname)
    }
}

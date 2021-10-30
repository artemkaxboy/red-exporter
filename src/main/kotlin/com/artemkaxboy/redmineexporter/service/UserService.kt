package com.artemkaxboy.redmineexporter.service

import com.artemkaxboy.redmineexporter.config.properties.RedmineProperties
import com.artemkaxboy.redmineexporter.entity.User
import com.artemkaxboy.redmineexporter.metrics.UserAddedEventListener
import com.artemkaxboy.redmineexporter.metrics.UserDeletedEventListener
import com.artemkaxboy.redmineexporter.repository.UserRepository
import mu.KotlinLogging
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class UserService(

    private val redmineProperties: RedmineProperties,
    private val userRepository: UserRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    private var users = emptyList<User>()

    /**
     * Fetches all opened versions for all project ids listed in properties.
     */
    fun fetchAllUsers() {

        fetchUsers(redmineProperties.users)
    }

    /**
     * Returns all versions for all projects.
     */
    fun getAllUsers(): List<User> = users

    private fun fetchUsers(userIds: List<Long>) {

        users = userRepository.findByIdIn(userIds)
            .also { fetchedUsers -> notifyChanges(users, fetchedUsers) }
    }

    private fun notifyChanges(existingUsers: List<User>, loadedUsers: List<User>) {
        notifyDeletedUsers(existingUsers, loadedUsers)
        notifyAddedUsers(existingUsers, loadedUsers)
    }

    private fun notifyDeletedUsers(existingUsers: List<User>, loadedUsers: List<User>) {
        (existingUsers - loadedUsers)
            .sortedBy { it.id } // for better logs reading
            .forEach { deletedUser ->

                logger.info { "User deleted: (#${deletedUser.id} ${deletedUser.login})" }
                applicationEventPublisher.publishEvent(UserDeletedEventListener.Event(user = deletedUser))
            }
    }

    private fun notifyAddedUsers(existingUsers: List<User>, loadedUsers: List<User>) {
        (loadedUsers - existingUsers)
            .sortedBy { it.id } // for better logs reading
            .forEach { addedUser ->

                logger.info { "User added: (#${addedUser.id} ${addedUser.name})" }
                applicationEventPublisher.publishEvent(UserAddedEventListener.Event(user = addedUser))
            }
    }
}

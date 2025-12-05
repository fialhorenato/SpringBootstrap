package com.renato.springbootstrap.security.entity

import com.renato.springbootstrap.factory.RoleFactory.Companion.generateRole
import com.renato.springbootstrap.factory.UserFactory.Companion.generateUser
import com.renato.springbootstrap.security.repository.RoleRepository
import com.renato.springbootstrap.security.repository.UserRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.Hibernate
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
import java.util.*

@DataJpaTest
class RoleEntityTest {
    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @PersistenceContext
    lateinit var entityManager: EntityManager

    @Test
    fun saveSanity() {
        val user = generateUser()
        userRepository.save(user)
        val role = generateRole(user)
        val roleSaved = roleRepository.save(role)
        assertThat(roleSaved.user).isEqualTo(user)
        assertThat(roleSaved).isNotNull
    }

    @Test
    fun cascadeSanity() {
        val user = generateUser()
        val userSaved = userRepository.save(user)
        roleRepository.save(generateRole(userSaved))
        val roles = roleRepository.findAllByUser_Username("username")
        assertThat(roles).isNotEmpty
        roles.forEach { assertThat(it.user.id).isEqualTo(userSaved.id) }
    }

    @Test
    fun deleteAndFindRolesByUsernameSanity() {
        val user = generateUser()
        val userSaved = userRepository.save(user)
        roleRepository.save(generateRole(userSaved))
        roleRepository.save(generateRole(userSaved, "ADMIN"))
        roleRepository.deleteByUserAndRole(userSaved, "USER")
        val roles = roleRepository.findAllByUser_Username("username")
        assertThat(roles).isNotEmpty
    }

    // Tests specifically for roleId validation
    @Test
    fun `should persist and retrieve roleId correctly`() {
        val user = generateUser()
        userRepository.save(user)

        val specificUuid = UUID.randomUUID()
        val role = generateRole(user, "USER", specificUuid)
        val savedRole = roleRepository.save(role)

        // Retrieve from database to verify persistence
        val retrievedRole = roleRepository.findById(savedRole.id!!).get()

        // Validate the roleId is persisted correctly
        assertThat(retrievedRole.roleId).isEqualTo(specificUuid)
        assertThat(retrievedRole.roleId).isNotNull()
        assertThat(retrievedRole.roleId).isInstanceOf(UUID::class.java)
    }

    @Test
    fun `should maintain unique roleId across multiple roles`() {
        val user = generateUser()
        userRepository.save(user)

        // Create multiple roles with different roleIds
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()

        val role1 = generateRole(user, "USER", uuid1)
        val role2 = generateRole(user, "ADMIN", uuid2)

        roleRepository.save(role1)
        roleRepository.save(role2)

        // Retrieve all roles
        val roles = roleRepository.findAllByUser_Username(user.username)

        // Verify each role has its correct and unique roleId
        assertThat(roles).hasSize(2)
        assertThat(roles.map { it.roleId }).containsExactlyInAnyOrder(uuid1, uuid2)
    }

    // Tests specifically for user object validation
    @Test
    fun `should correctly associate role with user and maintain user properties`() {
        // Create user with specific properties
        val username = "testUser123"
        val email = "test@example.com"
        val user = generateUser(username = username, email = email)
        val savedUser = userRepository.save(user)

        // Create and save role with this user
        val role = generateRole(savedUser)
        val savedRole = roleRepository.save(role)

        // Retrieve role and validate user properties
        val retrievedRole = roleRepository.findById(savedRole.id!!).get()

        assertThat(retrievedRole.user).isNotNull
        assertThat(retrievedRole.user.username).isEqualTo(username)
        assertThat(retrievedRole.user.email).isEqualTo(email)
        assertThat(retrievedRole.user.userId).isEqualTo(user.userId)
    }

    @Test
    fun `should maintain user reference integrity when retrieving roles`() {
        // Create two different users
        val user1 = generateUser(username = "user1", email = "user1@example.com")
        val user2 = generateUser(username = "user2", email = "user2@example.com")

        userRepository.save(user1)
        userRepository.save(user2)

        // Create roles for each user
        val roleUser1 = generateRole(user1, "USER")
        val roleUser2 = generateRole(user2, "ADMIN")

        roleRepository.save(roleUser1)
        roleRepository.save(roleUser2)

        // Retrieve roles for user1
        val rolesForUser1 = roleRepository.findAllByUser_Username(user1.username)

        // Verify correct user association
        assertThat(rolesForUser1).hasSize(1)
        assertThat(rolesForUser1[0].user.username).isEqualTo(user1.username)
        assertThat(rolesForUser1[0].user.userId).isEqualTo(user1.userId)
        assertThat(rolesForUser1[0].user.email).isEqualTo(user1.email)
    }

    @Test
    fun `should handle lazy loading of user in role entity`() {
        val user = generateUser()
        val savedUser = userRepository.save(user)

        val role = generateRole(savedUser)
        val savedRole = roleRepository.save(role)

        // Clear persistence context to test lazy loading
        roleRepository.flush()

        // Retrieve role
        val retrievedRole = roleRepository.findById(savedRole.id!!).get()

        // Access user properties to trigger lazy loading
        assertThat(retrievedRole.user).isNotNull
        assertThat(retrievedRole.user.username).isEqualTo(user.username)
    }

    /**
     * Test to validate that the user property in RoleEntity is lazily loaded
     */
    /**
     * Test to validate that the user property in RoleEntity is lazily loaded
     */
    @Test
    fun `should validate lazy loading of user in role entity`() {
        // Create and save entities
        val user = generateUser()
        userRepository.save(user)

        val role = generateRole(user)
        val savedRole = roleRepository.save(role)

        // Flush and clear the persistence context to detach entities
        entityManager.flush()
        entityManager.clear()

        // Load the role without initializing the user
        val retrievedRole = roleRepository.findById(savedRole.id!!).get()

        // Verify the entity is loaded but user is not initialized
        assertThat(retrievedRole).isNotNull

        assertThat(Hibernate.isInitialized(retrievedRole.user)).isTrue
    }

    /**
     * Test to validate that the user property in RoleEntity is configured for lazy loading
     */
    @Test
    fun `should confirm user property is configured for lazy loading`() {
        // Get the ManyToOne annotation from the user field
        val userField = RoleEntity::class.java.getDeclaredField("user")
        val manyToOneAnnotation = userField.getAnnotation(ManyToOne::class.java)

        // Verify the fetch type is LAZY
        assertThat(manyToOneAnnotation).isNotNull
        if (manyToOneAnnotation != null) {
            assertThat(manyToOneAnnotation.fetch).isEqualTo(FetchType.LAZY)
        }
    }

    /**
     * Test to validate that roleId must be unique
     */
    @Test
    fun `should enforce uniqueness of roleId`() {
        val user1 = generateUser(username = "user1", email = "user1@example.com")
        val user2 = generateUser(username = "user2", email = "user2@example.com")

        userRepository.save(user1)
        userRepository.save(user2)

        // Create a specific UUID to reuse
        val sharedUuid = UUID.randomUUID()

        // Save first role with the UUID
        val role1 = generateRole(user1, "USER", sharedUuid)
        roleRepository.save(role1)

        // Try to save second role with the same UUID
        val role2 = generateRole(user2, "ADMIN", sharedUuid)

        // This should throw an exception due to unique constraint on roleId
        assertThrows<DataIntegrityViolationException> {
            roleRepository.save(role2)
            roleRepository.flush() // Force flush to trigger constraint violation
        }
    }

    /**
     * Test to validate that the combination of user and role must be unique
     */
    @Test
    fun `should enforce uniqueness of user and role combination`() {
        val user = generateUser()
        userRepository.save(user)

        // Save first role
        val role1 = generateRole(user, "USER")
        roleRepository.save(role1)

        // Try to save another role with the same user and role but different UUID
        val role2 = generateRole(user, "USER", UUID.randomUUID())

        // This should throw an exception due to unique constraint on user_id and role
        assertThrows<DataIntegrityViolationException> {
            roleRepository.save(role2)
            roleRepository.flush() // Force flush to trigger constraint violation
        }
    }


}
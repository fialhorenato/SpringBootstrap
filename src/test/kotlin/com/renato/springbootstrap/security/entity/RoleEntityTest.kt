package com.renato.springbootstrap.security.entity

import com.renato.springbootstrap.factory.RoleFactory.Companion.generateRole
import com.renato.springbootstrap.factory.UserFactory.Companion.generateUser
import com.renato.springbootstrap.security.repository.RoleRepository
import com.renato.springbootstrap.security.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.util.UUID

@DataJpaTest
class RoleEntityTest {
    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var userRepository: UserRepository

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
}
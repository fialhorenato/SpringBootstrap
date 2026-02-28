package com.renato.springbootstrap.security.entity

import com.renato.springbootstrap.factory.RoleFactory
import com.renato.springbootstrap.factory.UserFactory
import com.renato.springbootstrap.security.repository.RoleRepository
import com.renato.springbootstrap.security.repository.UserRepository
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
import java.util.UUID

@DataJpaTest
class RoleEntityTest {

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `given_role_and_user_when_saved_then_role_is_persisted_with_user_link`() {
        val user = userRepository.save(UserFactory.createUser())
        val role = RoleFactory.createRole(user, "USER")

        val saved = roleRepository.save(role)

        assertThat(saved.id).isNotNull
        assertThat(saved.user.id).isEqualTo(user.id)
        assertThat(saved.role).isEqualTo("USER")
    }

    @Test
    fun `given_roles_for_multiple_users_when_find_all_by_username_then_only_matching_roles_are_returned`() {
        val user1 = userRepository.save(UserFactory.createUser(username = "user1", email = "user1@example.com"))
        val user2 = userRepository.save(UserFactory.createUser(username = "user2", email = "user2@example.com"))

        roleRepository.save(RoleFactory.createRole(user1, "USER"))
        roleRepository.save(RoleFactory.createRole(user1, "ADMIN"))
        roleRepository.save(RoleFactory.createRole(user2, "USER"))

        val roles = roleRepository.findAllByUser_Username("user1")

        assertThat(roles).hasSize(2)
        assertThat(roles.map { it.role }).containsExactlyInAnyOrder("USER", "ADMIN")
    }

    @Test
    fun `given_user_roles_when_delete_by_user_and_role_then_only_target_role_is_removed`() {
        val user = userRepository.save(UserFactory.createUser())
        roleRepository.save(RoleFactory.createRole(user, "USER"))
        roleRepository.save(RoleFactory.createRole(user, "ADMIN"))

        roleRepository.deleteByUserAndRole(user, "USER")

        val roles = roleRepository.findAllByUser_Username(user.username)
        assertThat(roles.map { it.role }).containsExactly("ADMIN")
    }

    @Test
    fun `given_duplicate_role_id_when_persisting_second_role_then_data_integrity_violation_is_thrown`() {
        val sharedRoleId = UUID.randomUUID()
        val user1 = userRepository.save(UserFactory.createUser(username = "user1", email = "user1@example.com"))
        val user2 = userRepository.save(UserFactory.createUser(username = "user2", email = "user2@example.com"))

        roleRepository.save(RoleFactory.createRole(user1, "USER", sharedRoleId))

        assertThrows<DataIntegrityViolationException> {
            roleRepository.save(RoleFactory.createRole(user2, "ADMIN", sharedRoleId))
            roleRepository.flush()
        }
    }

    @Test
    fun `given_duplicate_user_role_pair_when_persisting_second_role_then_data_integrity_violation_is_thrown`() {
        val user = userRepository.save(UserFactory.createUser())
        roleRepository.save(RoleFactory.createRole(user, "USER"))

        assertThrows<DataIntegrityViolationException> {
            roleRepository.save(RoleFactory.createRole(user, "USER", UUID.randomUUID()))
            roleRepository.flush()
        }
    }

    @Test
    fun `given_role_entity_metadata_when_inspected_then_user_association_is_lazy`() {
        val userField = RoleEntity::class.java.getDeclaredField("user")
        val annotation = userField.getAnnotation(ManyToOne::class.java)

        assertThat(annotation).isNotNull
        assertThat(annotation.fetch).isEqualTo(FetchType.LAZY)
    }

    @Test
    fun `given_equal_role_entities_when_compared_then_equals_and_hashcode_match`() {
        val user = UserFactory.createUser(username = "user", email = "user@example.com")
        val role1 = RoleFactory.createRole(user, "USER", UUID.randomUUID())
        val role2 = role1.copy()

        assertThat(role1).isEqualTo(role2)
        assertThat(role1.hashCode()).isEqualTo(role2.hashCode())
    }

    @Test
    fun `given_role_entity_when_destructured_and_copied_then_components_and_copy_are_correct`() {
        val user = UserFactory.createUser(username = "user", email = "user@example.com")
        val role = RoleFactory.createRole(user, "USER")

        val (id, roleId, roleUser, roleName, createdAt, updatedAt) = role
        val updatedRole = role.copy(role = "ADMIN")

        assertThat(id).isEqualTo(role.id)
        assertThat(roleId).isEqualTo(role.roleId)
        assertThat(roleUser).isEqualTo(user)
        assertThat(roleName).isEqualTo("USER")
        assertThat(createdAt).isEqualTo(role.createdAt)
        assertThat(updatedAt).isEqualTo(role.updatedAt)
        assertThat(updatedRole.role).isEqualTo("ADMIN")
        assertThat(role.role).isEqualTo("USER")
    }

    @Test
    fun `given_role_entity_when_mutable_properties_are_updated_then_new_values_are_applied`() {
        val user = UserFactory.createUser(username = "user", email = "user@example.com")
        val otherUser = UserFactory.createUser(username = "other", email = "other@example.com")
        val role = RoleFactory.createRole(user, "USER")
        val newRoleId = UUID.randomUUID()

        role.id = 42L
        role.roleId = newRoleId
        role.user = otherUser
        role.role = "ADMIN"
        role.createdAt = role.createdAt?.plusSeconds(60)
        role.updatedAt = role.updatedAt?.plusSeconds(120)

        assertThat(role.id).isEqualTo(42L)
        assertThat(role.roleId).isEqualTo(newRoleId)
        assertThat(role.user).isEqualTo(otherUser)
        assertThat(role.role).isEqualTo("ADMIN")
        assertThat(role.createdAt).isNotNull
        assertThat(role.updatedAt).isNotNull
    }
}

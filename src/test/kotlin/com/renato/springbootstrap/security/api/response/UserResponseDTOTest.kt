package com.renato.springbootstrap.security.api.response

import com.renato.springbootstrap.factory.RoleFactory
import com.renato.springbootstrap.factory.UserFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserResponseDTOTest {

    @Test
    fun `given_user_entity_with_roles_when_constructor_is_called_then_roles_are_mapped`() {
        val baseUser = UserFactory.createUser(username = "username", email = "email")
        val user = baseUser.copy(
            roles = listOf(
                RoleFactory.createRole(baseUser, "USER"),
                RoleFactory.createRole(baseUser, "ADMIN"),
            ),
        )

        val response = UserResponseDTO(user)

        assertThat(response.username).isEqualTo("username")
        assertThat(response.email).isEqualTo("email")
        assertThat(response.roles).containsExactlyInAnyOrder("USER", "ADMIN")
    }
}

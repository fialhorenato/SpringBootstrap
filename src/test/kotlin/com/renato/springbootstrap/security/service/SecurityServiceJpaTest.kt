package com.renato.springbootstrap.security.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@SpringBootTest(properties = ["spring.jpa.show-sql=false"])
class SecurityServiceJpaTest {

    @Autowired
    lateinit var userService: UserServiceImpl

    @Test
    fun `given_created_user_when_admin_role_is_added_then_both_roles_are_persisted`() {
        val suffix = UUID.randomUUID().toString().take(8)
        val username = "user_$suffix"
        val email = "user_$suffix@example.com"

        userService.createUser(username, "password", email)
        userService.addRole(username, "ADMIN")

        val roles = userService.findAllRolesByUsername(username)

        assertThat(roles.map { it.role }).containsExactlyInAnyOrder("USER", "ADMIN")
    }
}

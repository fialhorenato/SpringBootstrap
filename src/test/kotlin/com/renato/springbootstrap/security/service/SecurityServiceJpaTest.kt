package com.renato.springbootstrap.security.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    properties = ["spring.jpa.show-sql=true"]
)
class SecurityServiceJpaTest {
    @Autowired
    lateinit var securityService: UserServiceImpl

    @Test
    fun rolesSanity() {
        securityService.createUser("username", "password", "email")
        securityService.addRole("username", "ADMIN")
        val roles = securityService.findAllRolesByUsername("username")
        assertThat(roles.size).isEqualTo(2)
    }
}
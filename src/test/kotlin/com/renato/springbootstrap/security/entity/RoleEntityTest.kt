package com.renato.springbootstrap.security.entity

import com.renato.springbootstrap.factory.RoleFactory.Companion.generateRole
import com.renato.springbootstrap.factory.UserFactory.Companion.generateUser
import com.renato.springbootstrap.security.repository.RoleRepository
import com.renato.springbootstrap.security.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class RoleEntityTest {
    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun saveSanity() {
        val user = generateUser()
        val userSaved = userRepository.save(user)
        val role = generateRole(userSaved)
        val roleSaved = roleRepository.save(role)
        assertThat(roleSaved).isNotNull
    }

    @Test
    fun cascadeSanity() {
        val user = generateUser()
        user.roles = listOf(generateRole(user))
        val userSaved = userRepository.save(user)
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
}
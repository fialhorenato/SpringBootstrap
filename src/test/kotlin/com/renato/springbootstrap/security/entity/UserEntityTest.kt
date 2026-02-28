package com.renato.springbootstrap.security.entity

import com.renato.springbootstrap.factory.UserFactory
import com.renato.springbootstrap.security.repository.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import java.time.Instant
import java.util.UUID

@DataJpaTest
class UserEntityTest {

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `given_user_when_save_then_user_is_persisted`() {
        val user = UserFactory.createUser()

        val saved = userRepository.save(user)

        assertThat(saved.id).isNotNull
        assertThat(saved.username).isEqualTo(user.username)
        assertThat(saved.email).isEqualTo(user.email)
    }

    @Test
    fun `given_existing_and_missing_username_when_findByUsername_then_return_expected_results`() {
        val user = UserFactory.createUser(username = "username")
        userRepository.save(user)

        val found = userRepository.findByUsername("username")
        val missing = userRepository.findByUsername("missing")

        assertThat(found).isNotNull
        assertThat(found?.username).isEqualTo("username")
        assertThat(missing).isNull()
    }

    @ParameterizedTest(name = "query username={0}, email={1} shouldExist={2}")
    @CsvSource(
        "username,email,true",
        "other,other@email,false",
    )
    fun `given_query_values_when_existsByUsernameOrEmail_then_return_expected_flag`(
        username: String,
        email: String,
        shouldExist: Boolean,
    ) {
        userRepository.save(UserFactory.createUser(username = "username", email = "email"))

        val exists = userRepository.existsByUsernameOrEmail(username, email)

        assertThat(exists).isEqualTo(shouldExist)
    }

    @Test
    fun `given_equal_user_entities_when_compared_then_equals_and_hashcode_match`() {
        val userId = UUID.randomUUID()
        val createdAt = Instant.now()
        val updatedAt = Instant.now()

        val user1 = UserFactory.createUser(
            username = "username",
            email = "email",
            password = "password",
            userId = userId,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
        val user2 = UserFactory.createUser(
            username = "username",
            email = "email",
            password = "password",
            userId = userId,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        assertThat(user1).isEqualTo(user2)
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode())
    }

    @Test
    fun `given_user_entity_when_destructured_and_copied_then_components_and_copy_are_correct`() {
        val user = UserFactory.createUser(username = "username", email = "email")

        val (id, userId, username, email, password, roles, createdAt, updatedAt) = user
        val copied = user.copy(email = "new@email.com")

        assertThat(id).isEqualTo(user.id)
        assertThat(userId).isEqualTo(user.userId)
        assertThat(username).isEqualTo("username")
        assertThat(email).isEqualTo("email")
        assertThat(password).isEqualTo("password")
        assertThat(roles).isEqualTo(user.roles)
        assertThat(createdAt).isEqualTo(user.createdAt)
        assertThat(updatedAt).isEqualTo(user.updatedAt)
        assertThat(copied.email).isEqualTo("new@email.com")
        assertThat(user.email).isEqualTo("email")
    }
}

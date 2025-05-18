package com.renato.springbootstrap.security.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
data class UserEntity (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id : Long? = null,

        @Column(name = "user_id", unique = true)
        val userId : UUID,

        val username : String,

        val email : String,

        val password : String,

        @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user")
        val roles : List<RoleEntity>,

        @CreatedDate
        @Column(name = "created_at")
        val createdAt : Instant? = Instant.now(),

        @LastModifiedDate
        @Column(name = "updated_at")
        val updatedAt : Instant? = Instant.now()
)
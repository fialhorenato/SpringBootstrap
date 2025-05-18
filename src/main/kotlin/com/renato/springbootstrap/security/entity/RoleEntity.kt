package com.renato.springbootstrap.security.entity

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "roles",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "role"])]
)
data class RoleEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "role_id", unique = true)
    var roleId: UUID,

    @ManyToOne(fetch = FetchType.LAZY)
    var user: UserEntity,

    var role: String,

    @Column(name = "created_at")
    var createdAt : Instant? = Instant.now(),

    @Column(name = "updated_at")
    var updatedAt : Instant? = Instant.now()
)
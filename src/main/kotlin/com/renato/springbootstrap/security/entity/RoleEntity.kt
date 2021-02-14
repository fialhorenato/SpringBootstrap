package com.renato.springbootstrap.security.entity

import java.time.Instant
import javax.persistence.*
import javax.persistence.FetchType.LAZY
import javax.persistence.GenerationType.IDENTITY

@Entity
@Table(name = "roles",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "role"])]
)
data class RoleEntity(
    @Id
    @GeneratedValue(strategy = IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = LAZY)
    var user: UserEntity?,

    var role: String,

    @Column(name = "created_at")
    var createdAt : Instant? = Instant.now(),

    @Column(name = "updated_at")
    var updatedAt : Instant? = Instant.now()
) {
    override fun toString(): String {
        return "RoleEntity(id=$id, user=${user?.username}, role='$role')"
    }
}
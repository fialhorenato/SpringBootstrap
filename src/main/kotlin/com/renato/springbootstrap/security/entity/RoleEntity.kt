package com.renato.springbootstrap.security.entity

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

    var role: String
)
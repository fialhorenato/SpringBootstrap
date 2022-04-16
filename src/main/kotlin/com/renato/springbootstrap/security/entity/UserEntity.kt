package com.renato.springbootstrap.security.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import javax.persistence.*
import javax.persistence.CascadeType.ALL

@Entity
@Table(name = "users")
data class UserEntity (
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id : Long? = null,

        var username : String,

        var email : String,

        var password : String,

        @OneToMany(cascade = [ALL], mappedBy = "user")
        var roles : List<RoleEntity>,

        @CreatedDate
        @Column(name = "created_at")
        var createdAt : Instant? = Instant.now(),

        @LastModifiedDate
        @Column(name = "updated_at")
        var updatedAt : Instant? = Instant.now()
)
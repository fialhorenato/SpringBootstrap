package com.renato.springbootstrap.security.entity

import com.renato.springbootstrap.security.entity.RoleEntity
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
        var roles : List<RoleEntity>
) {
        override fun toString(): String {
                return "UserEntity(id=$id, username='$username', email='$email', password='$password', roles=$roles)"
        }
}
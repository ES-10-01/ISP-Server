package com.isp.server.util

import java.security.MessageDigest
import com.isp.server.entites.UserEntity
import com.isp.server.services.UserService
import java.util.*

fun validateCredentials(userEntity: UserEntity, userService: UserService): Boolean {
        val storedUserEntity: Optional<UserEntity> = userService.getById(userEntity.uid)
        if (storedUserEntity.isEmpty)
                return false
        return hashUserPassword(userEntity) == storedUserEntity.get()
}
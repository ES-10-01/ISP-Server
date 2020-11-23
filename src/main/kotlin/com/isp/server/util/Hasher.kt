package com.isp.server.util

import com.isp.server.entites.UserEntity
import java.security.MessageDigest

fun hash(strToBeHashed: String): String {
        val bytes = strToBeHashed.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
}

fun hashUserPassword(userEntity: UserEntity): UserEntity {
        return UserEntity(
                userEntity.uid,
                hash(userEntity.password),
                userEntity.name,
                userEntity.surname,
                userEntity.privileges
        )
}
package com.isp.server.util

import com.isp.server.models.UserModel
import java.security.MessageDigest

fun hash(strToBeHashed : String): String {
    val bytes = strToBeHashed.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("", { str, it -> str + "%02x".format(it) })
}

fun generatePassword() : String {
    val passwordLength = 10
    val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    val randomString = (1..passwordLength)
        .map { _ -> kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("");

    return randomString
}

fun hashUserPassword(userModel: UserModel): UserModel {
    return UserModel(
        userModel.uid,
        hash(userModel.password),
        userModel.name,
        userModel.surname,
        userModel.privileges,
        userModel.availableLocks
    )
}
package com.isp.server.util

import java.security.MessageDigest

fun hash(strToBeHashed : String): String {
    val bytes = strToBeHashed.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("", { str, it -> str + "%02x".format(it) })
}
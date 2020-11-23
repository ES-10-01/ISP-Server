package com.isp.server.entites

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class UserEntity(
    @Id
    val uid: Int, // ? = null
    val password: String,
    val name: String,
    val surname: String,
    val privileges: String
    //val availableLocks: Array<Lock>
)
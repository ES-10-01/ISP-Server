package com.isp.server.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "user")
data class UserModel (
    @Id
    val uid: Int,
    val password: String,
    val name: String,
    val surname: String,
    val privileges: String,
    val availableLocks: List<Int>
)

package com.isp.server.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(
        @Id
        val uid: Int, // ? = null
        val password: String,
        val name: String,
        val surname: String,
        val privileges: String
        //val availableLocks: Array<Lock>
)

@Document
data class Lock(
        @Id
        val uid: Int,
        val name: String,
        val ip: String
)

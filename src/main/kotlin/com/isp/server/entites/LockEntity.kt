package com.isp.server.entites

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class LockEntity(
        @Id
        val uid: Int,
        val name: String,
        val ip: String
)
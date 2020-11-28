package com.isp.server.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

data class Credentials (
        val user_uid: Int,
        val password: String
)
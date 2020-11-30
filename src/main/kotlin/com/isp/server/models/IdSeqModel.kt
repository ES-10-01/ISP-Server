package com.isp.server.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "idSequences")
data class IdSeqModel (
    @Id
    val id: String? = null,
    val seq: Int
)

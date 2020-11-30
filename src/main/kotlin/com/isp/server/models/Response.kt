package com.isp.server.models

data class Response<T> (
    val status: String,
    val message: String,
    val data: T? = null
)

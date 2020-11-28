package com.isp.server.models

enum class ResponseMessages(val text: String) {
        SUCCESS("Success"),
        CREDENTIALS_VALIDATION_ERROR("Error: you entered wrong credentials or you have not enough privileges")

}

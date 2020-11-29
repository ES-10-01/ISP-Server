package com.isp.server.models

enum class ResponseMessages(val text: String) {
        SUCCESS("Success"),
        CREDENTIALS_VALIDATION_ERROR("Error: you entered wrong credentials or you have not enough privileges"),
        USER_NOT_FOUND("Error: can't find user with given uid")
}

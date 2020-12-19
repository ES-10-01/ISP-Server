package com.isp.server.models

enum class ResponseMessages(val text: String) {
    SUCCESS("Success"),
    CREDENTIALS_VALIDATION_ERROR("Error: you entered wrong credentials or you have not enough privileges"),
    USER_NOT_FOUND("Error: can't find user with given uid"),
    NOTHING_TO_UPDATE("Error: there is nothing to update. Set reset_password to true or pass correct new_privileges"),
    NO_PASSWORD_SPECIFIED("Error: new_password is not specified"),
    WRONG_PRIVILEGES("Error: incorrect privileges"),
    LOCK_NOT_FOUND("Error: can't find lock with given uid"),
    LOCK_ALREADY_ADDED("Error: specified lock is already added"),
    NO_LOCK_FOR_GIVEN_USER("Error: user has no access to specified lock")
}

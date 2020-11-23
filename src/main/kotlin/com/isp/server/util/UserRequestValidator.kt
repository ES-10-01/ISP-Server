package com.isp.server.util

import com.isp.server.models.UserModel
import com.isp.server.services.UserService
import java.util.*

fun validateCredentials(userModel: UserModel, userService: UserService): Boolean {
        val storedUserModel: Optional<UserModel> = userService.getById(userModel.uid)
        if (storedUserModel.isEmpty)
                return false
        return hashUserPassword(userModel) == storedUserModel.get()
}
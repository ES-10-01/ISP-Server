package com.isp.server.util

import com.isp.server.models.Credentials
import com.isp.server.models.UserModel
import com.isp.server.services.UserService
import java.util.*

fun validateCredentials(credentials: Credentials, userService: UserService, admin: Boolean = false): Boolean {
        val storedUserModel: Optional<UserModel> = userService.getById(credentials.user_uid)
        if (storedUserModel.isEmpty || (admin && storedUserModel.get().privileges != "ADMIN"))
                return false
        return hash(credentials.password) == storedUserModel.get().password
}
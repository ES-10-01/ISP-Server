package com.isp.server.controllers

import com.isp.server.models.Credentials
import com.isp.server.models.Response
import com.isp.server.models.ResponseMessages
import com.isp.server.models.UserModel
import com.isp.server.services.UserService
import com.isp.server.util.hash
import com.isp.server.util.validateCredentials
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/user")
class UserController(private val userService: UserService) {

    @PostMapping("/login")
    fun login(@RequestBody requestBody: LoginRequest): Response<LoginResponse> {
        if (!validateCredentials(requestBody.credentials, userService, admin = false))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val user: Optional<UserModel> = userService.getById(requestBody.credentials.user_uid)
        val res = LoginResponse(privileges = user.get().privileges)

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text, data = res)
    }

    @PostMapping("/update")
    fun update(@RequestBody requestBody: UpdateRequest): Response<Nothing> {
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val userFromDatabase: Optional<UserModel> = userService.getById(requestBody.credentials.user_uid)
        var userToUpdate: UserModel = userFromDatabase.get()

        userToUpdate = userToUpdate.copy(password = hash(requestBody.new_password))
        userService.update(userToUpdate)

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text)
    }

    data class LoginRequest(
        val credentials: Credentials
    )

    data class LoginResponse(
        val privileges: String
    )

    data class UpdateRequest(
        val credentials: Credentials,
        val new_password: String
    )
}

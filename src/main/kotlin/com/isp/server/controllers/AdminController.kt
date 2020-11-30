package com.isp.server.controllers

import com.isp.server.models.Credentials
import com.isp.server.models.Response
import com.isp.server.models.ResponseMessages
import com.isp.server.models.UserModel
import com.isp.server.services.NextIdService
import com.isp.server.services.UserService
import com.isp.server.util.generatePassword
import com.isp.server.util.hashUserPassword
import com.isp.server.util.validateCredentials
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/admin")
class AdminController (private val userService: UserService, private val nextIdService : NextIdService) {
    val allowedPrivileges = arrayOf("USER", "ADMIN")

    @PostMapping("user/all")
    fun getAll(@RequestBody requestBody: GetAllUsersRequest): Response<List<UserModel>> {
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text, data = userService.getAll())
    }

    @PostMapping("user/add")
    fun create(@RequestBody requestBody: AddUserRequest): Response<UserModel> {
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val newUser = UserModel(
                nextIdService.getNextSequence("idSequences"),
                generatePassword(),
                requestBody.name,
                requestBody.surname,
                requestBody.privileges.toUpperCase()
        )
        userService.insert(hashUserPassword(newUser))
        return Response(status = "OK", message = ResponseMessages.SUCCESS.text, data = newUser)
    }

    @PostMapping("/user/delete")
    fun delete(@RequestBody requestBody: DeleteUserRequest): Response<Nothing> {
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val deletedUser: Optional<UserModel> = userService.deleteById(requestBody.target_user_uid)
        if (deletedUser.isEmpty)
            return Response(status = "DENIED", message = ResponseMessages.USER_NOT_FOUND.text)

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text)
    }

    @PostMapping("/user/update")
    fun update(@RequestBody requestBody: UpdateUserRequest): Response<UserModel> {
        if (!requestBody.reset_password && requestBody.new_privileges == null)
            return Response(status = "DENIED", message = ResponseMessages.NOTHING_TO_UPDATE.text)
        if (requestBody.new_privileges != null && !validatePrivileges(requestBody.new_privileges))
            return Response(status = "DENIED", message = ResponseMessages.WRONG_PRIVILEGES.text)
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val userToUpdate: Optional<UserModel> = userService.getById(requestBody.target_user_uid)
        if (userToUpdate.isEmpty)
            return Response(status = "DENIED", message = ResponseMessages.USER_NOT_FOUND.text)

        var updatedUser: UserModel = userToUpdate.get()
        var updatedUserToSave: UserModel = updatedUser.copy()

        if (requestBody.reset_password) {
            updatedUser = updatedUser.copy(password = generatePassword())
            updatedUserToSave = hashUserPassword(updatedUser)
        }
        if (requestBody.new_privileges != null) {
            updatedUser = updatedUserToSave.copy(privileges = requestBody.new_privileges.toUpperCase())
            updatedUserToSave = updatedUser.copy()
        }
        userService.update(updatedUserToSave)
        return Response(status = "OK", message = ResponseMessages.SUCCESS.text, data = updatedUser)
    }

    fun validatePrivileges(privileges: String): Boolean{
        return privileges.toUpperCase() in allowedPrivileges
    }

    data class GetAllUsersRequest(
            val credentials: Credentials,
            val page: Int = 10,
            val size: Int = 10
    )

    data class AddUserRequest(
            val credentials: Credentials,
            val name: String,
            val surname: String,
            val privileges: String
    )

    data class DeleteUserRequest(
            val credentials: Credentials,
            val target_user_uid: Int
    )

    data class UpdateUserRequest(
            val credentials: Credentials,
            val target_user_uid: Int,
            val reset_password: Boolean,
            val new_privileges: String? = null
    )

}

package com.isp.server.controllers

import com.isp.server.models.Credentials
import com.isp.server.models.Response
import com.isp.server.models.ResponseMessages
import com.isp.server.models.UserModel
import com.isp.server.services.NextIdService
import com.isp.server.services.UserService
import com.isp.server.util.hash
import com.isp.server.util.generatePassword
import com.isp.server.util.hashUserPassword
import com.isp.server.util.validateCredentials
import org.apache.catalina.User
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/admin")
class AdminController (private val userService: UserService, private val nextIdService : NextIdService) {

    @PostMapping("user/all")
    fun getAll(@RequestBody requestBody: GetAllUsersRequest): Response<List<UserModel>> {
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text, data = userService.getAll())
    }

    @PostMapping("user/add")
    fun create(@RequestBody requestBody: UserAddRequest): Response<UserModel> {
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val newUser = UserModel(
                nextIdService.getNextSequence("idSequences"),
                generatePassword(),
                requestBody.name,
                requestBody.surname,
                requestBody.privileges
        )
        userService.insert(hashUserPassword(newUser))
        return Response(status = "OK", message = ResponseMessages.SUCCESS.text, data = newUser)
    }

    @DeleteMapping("{id}")
    fun deleteByIsbn(@PathVariable id: Int): Optional<UserModel> = userService.deleteById(id)

    @GetMapping("{id}")
    fun getById(@PathVariable id:Int): Optional<UserModel> = userService.getById(id)

    data class UserAddRequest(
            val credentials: Credentials,
            val name: String,
            val surname: String,
            val privileges: String
    )

    data class GetAllUsersRequest(
            val credentials: Credentials,
            val page: Int = 10,
            val size: Int = 10
    )

}
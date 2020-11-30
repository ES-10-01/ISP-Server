package com.isp.server.controllers

import com.isp.server.models.UserModel
import com.isp.server.services.UserService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/user")
class UserController(private val userService: UserService) {
    @PostMapping
    fun update(@RequestBody user: UserModel): UserModel {
//        user.password = hash("dsc")
        return userService.update(user)
    }
}

package com.isp.server.controllers

import com.isp.server.entites.UserEntity
import com.isp.server.services.UserService
import com.isp.server.util.hash
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("api/user")
class UserController (private val userService: UserService) {
    @PostMapping
    fun update(@RequestBody user: UserEntity) : UserEntity {
//        user.password = hash("dsc")
        return userService.update(user)
    }
}

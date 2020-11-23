package com.isp.server.controllers

import com.isp.server.models.UserModel
import com.isp.server.services.NextIdService
import com.isp.server.services.UserService
import com.isp.server.util.hash
import com.isp.server.util.newPass
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/admin")
class AdminController (private val userService: UserService, private val nextIdService : NextIdService) {

    @GetMapping("user/all")
    fun getAll(pageable: Pageable):
        Page<UserModel> = userService.getAll(pageable)

    @PostMapping("user/add")
    fun create(/*@RequestBody credentials: UserModel*/): UserModel {
        val newUser : UserModel = UserModel(nextIdService.getNextSequence("idSequences"), newPass(), "Dima", "Vector", "USER")
        userService.insert(newUser.copy(password = hash(newUser.password)))
        return newUser
    }

    @DeleteMapping("{id}")
    fun deleteByIsbn(@PathVariable id: Int): Optional<UserModel> = userService.deleteById(id)

    @GetMapping("{id}")
    fun getById(@PathVariable id:Int): Optional<UserModel> = userService.getById(id)
}
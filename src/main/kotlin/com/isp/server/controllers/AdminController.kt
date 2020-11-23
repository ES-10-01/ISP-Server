package com.isp.server.controllers

import com.isp.server.entites.UserEntity
import com.isp.server.services.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/admin")
class AdminController (private val userService: UserService) {

    @GetMapping("user/all")
    fun getAll(pageable: Pageable):
        Page<UserEntity> = userService.getAll(pageable)

    @PostMapping("user/add")
    fun create(@RequestBody user: UserEntity): UserEntity = userService.insert(user)

    @DeleteMapping("{id}")
    fun deleteByIsbn(@PathVariable id: String): Optional<UserEntity> = userService.deleteById(id)

    @GetMapping("{id}")
    fun getById(@PathVariable id:String): Optional<UserEntity> = userService.getById(id)
}
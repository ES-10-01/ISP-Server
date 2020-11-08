package com.isp.server.controllers

import com.isp.server.models.User
import com.isp.server.services.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/user")
class UserController (private val userService: UserService) {

    @GetMapping
    fun getAll(pageable: Pageable): Page<User> = userService.getAll(pageable)

    @GetMapping("{id}")
    fun getById(@PathVariable id:String): Optional<User> = userService.getById(id)

    @PostMapping
    fun insert(@RequestBody user: User): User = userService.insert(user)

    @DeleteMapping("{id}")
    fun deleteByIsbn(@PathVariable id: String): Optional<User> = userService.deleteById(id)
}

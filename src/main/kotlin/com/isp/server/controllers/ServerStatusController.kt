package com.isp.server.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/ping")
class ServerStatusController {

        @GetMapping
        fun test(): String {
                return "Server is up!"
        }
}
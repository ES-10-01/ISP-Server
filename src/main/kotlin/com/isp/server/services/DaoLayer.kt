package com.isp.server.services

import org.springframework.data.mongodb.repository.MongoRepository
import com.isp.server.models.User

interface UserDAO: MongoRepository<User, String>

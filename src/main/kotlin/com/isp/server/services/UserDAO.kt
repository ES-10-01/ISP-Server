package com.isp.server.services

import org.springframework.data.mongodb.repository.MongoRepository
import com.isp.server.models.UserModel

interface UserDAO: MongoRepository<UserModel, Int>

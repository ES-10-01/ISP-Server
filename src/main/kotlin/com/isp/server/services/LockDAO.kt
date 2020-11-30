package com.isp.server.services

import com.isp.server.models.LockModel
import org.springframework.data.mongodb.repository.MongoRepository

interface LockDAO: MongoRepository<LockModel, Int>

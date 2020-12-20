package com.isp.server.services

import com.isp.server.models.LockModel
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface LockDAO: MongoRepository<LockModel, Int> {
    fun findByTCPConnId(TCPConnId : String) : Optional<LockModel>
}

package com.isp.server.tcp

import com.isp.server.models.LockModel
import com.isp.server.services.LockService
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class LockManager(private val lockService: LockService, @Lazy private val tcpConfiguration: TCPConfiguration) {
    fun addOrUpdate(uid : Int, TCPConnId : String, ip : String) {
        val lock = LockModel(uid, "name",  TCPConnId, ip)
        if (lock == lockService.update(lock))
            tcpConfiguration.sendMessage("LOL_SAVED", TCPConnId)
    }
}
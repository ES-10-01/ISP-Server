package com.isp.server.tcp

import com.isp.server.models.LockModel
import com.isp.server.services.LockService
import org.springframework.context.annotation.Lazy
import org.springframework.integration.ip.IpHeaders
import org.springframework.messaging.Message
import org.springframework.stereotype.Service
import java.util.*

@Service
class LockManager(private val lockService: LockService, @Lazy private val tcpConfiguration: TCPConfiguration) {
    fun sortIncomingMessage(msg: Message<String>) {
        val message = msg.payload.toString()
        if (message.startsWith("GOS_HELLO")) {
            addOrUpdate(message.substring(message.indexOf(",") + 1).toInt(),
                msg.getHeaders().get(IpHeaders.CONNECTION_ID, String::class.java)!!,
                message.substring(message.indexOf("=") + 1, message.indexOf(",")))
        } else if (message.startsWith("GOS_PASS")) {

        }
    }

    fun addOrUpdate(uid : Int, TCPConnId : String, ip : String) {
        val lockOptional : Optional<LockModel> = lockService.getById(uid)
        if (lockOptional.isEmpty) {
            lockService.insert(LockModel(uid = uid, TCPConnId = TCPConnId, ip = ip))
        } else {
            val tmp_name = lockOptional.get().name
            lockService.deleteById(uid)
            lockService.insert(LockModel(uid = uid, name = tmp_name, TCPConnId = TCPConnId, ip = ip))
            /*val lock = lockOptional.get() // TODO
            lock.TCPConnId = TCPConnId
            lock.ip = ip
            lockService.update(lock)*/
        }
    }

    fun requestPassword(uid : Int) {
        tcpConfiguration.sendMessage("GOS_GET", lockService.getById(uid).get().TCPConnId)
    }
}
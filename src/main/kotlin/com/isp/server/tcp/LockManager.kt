package com.isp.server.tcp

import com.isp.server.models.LockModel
import com.isp.server.models.LockStatuses
import com.isp.server.services.LockService
import org.springframework.context.annotation.Lazy
import org.springframework.integration.ip.IpHeaders
import org.springframework.messaging.Message
import org.springframework.stereotype.Service
import java.util.*

@Service
class LockManager(private val lockService: LockService, @Lazy private val tcpConfiguration: TCPConfiguration) {

    var requestedPINs = mutableMapOf<Triple<Int, String, LockStatuses>, Int>()  // Map: { Triple{lock_uid, PIN, lock_status}, user_uid }
    var locksInGetPassMode = mutableSetOf<Int>()                               // List: { lock_uid }

    fun sortIncomingMessage(msg: Message<String>) {
        val message = msg.payload.toString()
        if (message.startsWith("GOS_HELLO")) {
            addOrUpdate(message.substring(message.indexOf(",") + 1).toInt(),
                msg.getHeaders().get(IpHeaders.CONNECTION_ID, String::class.java)!!,
                message.substring(message.indexOf("=") + 1, message.indexOf(",")))
        } else if (message.startsWith("GOS_PASS")) {
            verifyPIN(message.substring(message.indexOf("=") + 1),
                msg.getHeaders().get(IpHeaders.CONNECTION_ID, String::class.java)!!)
        } else if (message.equals("GOS_CLOSE")) {
            timedClose(msg.getHeaders().get(IpHeaders.CONNECTION_ID, String::class.java)!!)
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

    fun verifyPIN(lockPIN : String, TCPConnId : String) {
        val lockOptional : Optional<LockModel> = lockService.getByTCPConnId(TCPConnId)

        // TODO [THINK] на самом деле при нашем флоу детектить неверно введённый пин при размере пула >1 кажется анриал (Арсу в 5:17 утра)
        when {
            requestedPINs.containsKey(Triple(lockOptional.get().uid, lockPIN, LockStatuses.PENDING)) -> {
                requestedPINs[Triple(lockOptional.get().uid, lockPIN, LockStatuses.OPENED)] = requestedPINs[Triple(lockOptional.get().uid, lockPIN, LockStatuses.PENDING)]!!
                requestedPINs.remove(Triple(lockOptional.get().uid, lockPIN, LockStatuses.PENDING))
            }
            requestedPINs.containsKey(Triple(lockOptional.get().uid, lockPIN, LockStatuses.BLOCKED)) -> {
                requestedPINs[Triple(lockOptional.get().uid, lockPIN, LockStatuses.OPENED)] = requestedPINs[Triple(lockOptional.get().uid, lockPIN, LockStatuses.BLOCKED)]!!
                requestedPINs.remove(Triple(lockOptional.get().uid, lockPIN, LockStatuses.BLOCKED))
            }
            else -> { requestPIN(lockOptional.get().uid); return }
        }

        open(TCPConnId)
    }

    fun requestPIN(uid : Int) {
        tcpConfiguration.sendMessage("GOS_GET", lockService.getById(uid).get().TCPConnId)
        locksInGetPassMode.add(uid)
    }

    fun open(TCPConnId : String) {
        tcpConfiguration.sendMessage("GOS_OPEN", TCPConnId)
    }

    fun timedClose(TCPConnId : String) {
        val lockOptional : Optional<LockModel> = lockService.getByTCPConnId(TCPConnId)

        var openedSessions = 0

        requestedPINs.forEach {
            if (it.key.first == lockOptional.get().uid) {
                openedSessions++
                if (it.key.third == LockStatuses.OPENED) {
                    requestedPINs.remove(it.key)
                    openedSessions--
                }
            }
        }

        if (openedSessions == 0) locksInGetPassMode.remove(lockOptional.get().uid)
    }
}
package com.isp.server.controllers

import com.isp.server.models.Credentials
import com.isp.server.models.Response
import com.isp.server.models.ResponseMessages
import com.isp.server.models.UserModel
import com.isp.server.services.LockService
import com.isp.server.services.UserService
import com.isp.server.tcp.LockManager
import com.isp.server.util.validateCredentials
import org.springframework.messaging.MessageHandlingException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*
import kotlin.random.Random

@RestController
@RequestMapping("api/lock")
class LockController(private val userService: UserService, private val lockService: LockService, private val lockManager: LockManager) {

    companion object {
        var currentPINs = mutableMapOf<Pair<Int, String>, Int>()    // Map: { Pair{lock_uid, PIN}, user_uid }
    }

    @PostMapping("/all")
    fun getAll(@RequestBody requestBody: GetAllRequest): Response<List<GetAllResponse>> {
        if (!validateCredentials(requestBody.credentials, userService, admin = false))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val targetUser: Optional<UserModel> = userService.getById(requestBody.credentials.user_uid)
        val userLocks: MutableList<Int> = targetUser.get().availableLocks

        val availableLocks = lockService.getAll().filter{ it.uid in userLocks.toIntArray() }
        val res: List<GetAllResponse> = availableLocks.map {
            GetAllResponse(lock_uid = it.uid, lock_name = it.name)
        }

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text, data = res)
    }

    @PostMapping("/open")
    fun open(@RequestBody requestBody: OpenRequest): Response<OpenResponse> {
        if (!validateCredentials(requestBody.credentials, userService, admin = false))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val user = userService.getById(requestBody.credentials.user_uid).get()
        if (currentPINs.containsValue(user.uid))
            return Response(status = "DENIED", message = ResponseMessages.PREVIOUS_SESSION_HAS_NOT_BEEN_CANCELED.text)

        val lockOptional = lockService.getById(requestBody.lock_uid)
        if (lockOptional.isEmpty) return Response(status = "DENIED", message = ResponseMessages.LOCK_NOT_FOUND.text)

        if (!user.availableLocks.contains(lockOptional.get().uid))
            return Response(status = "DENIED", message = ResponseMessages.NO_LOCK_FOR_GIVEN_USER.text)

        var lockPIN : String
        do {
            lockPIN = Random.nextInt(1000, 9999).toString()
        } while (lockOptional.get().uid to lockPIN in currentPINs)

        currentPINs[lockOptional.get().uid to lockPIN] = user.uid

        try {
            lockManager.requestPassword(lockOptional.get().uid)
        } catch (exception: MessageHandlingException) {
            return Response(status = "DENIED", message = ResponseMessages.LOCK_UNREACHABLE.text)
        }

        println(currentPINs.toString()) // TODO: remove after checking w/ several active users

        return Response(status = "OK", message = ResponseMessages.SENDING_PIN.text, data = OpenResponse(lockOptional.get().uid, lockPIN))
    }

    @PostMapping("/status")
    fun status(@RequestBody requestBody: StatusRequest): Response<Nothing> {
        if (!validateCredentials(requestBody.credentials, userService, admin = false))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text)
    }

    @PostMapping("/cancel")
    fun cancel(@RequestBody requestBody: CancelRequest): Response<Nothing> {
        if (!validateCredentials(requestBody.credentials, userService, admin = false))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text)
    }

    data class GetAllRequest(
        val credentials: Credentials
    )

    data class GetAllResponse(
        val lock_uid: Int,
        val lock_name: String
    )

    data class OpenRequest(
        val credentials: Credentials,
        val lock_uid: Int
    )

    data class OpenResponse(
        val lock_uid: Int,
        val lock_PIN: String
    )

    data class StatusRequest(
        val credentials: Credentials,
        val lock_uid: Int
    )

    data class StatusResponse(
        val todo: Nothing
    )

    data class CancelRequest(
        val credentials: Credentials,
        val lock_uid: Int
    )

    data class CancelResponse(
        val todo: Nothing
    )
}

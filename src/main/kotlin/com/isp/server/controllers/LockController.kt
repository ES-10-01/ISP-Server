package com.isp.server.controllers

import com.isp.server.models.Credentials
import com.isp.server.models.Response
import com.isp.server.models.ResponseMessages
import com.isp.server.models.UserModel
import com.isp.server.services.LockService
import com.isp.server.services.UserService
import com.isp.server.util.validateCredentials
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("api/lock")
class LockController(private val userService: UserService, private val lockService: LockService) {

    @PostMapping("/all")
    fun getAll(@RequestBody requestBody: GetAllRequest): Response<List<GetAllResponse>> {
        if (!validateCredentials(requestBody.credentials, userService, admin = false))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val targetUser: Optional<UserModel> = userService.getById(requestBody.credentials.user_uid)
        val userLocks: MutableList<Int> = targetUser.get().lock_uids

        val availableLocks = lockService.getAll().filter{ it.uid in userLocks.toIntArray() }
        val res: List<GetAllResponse> = availableLocks.map {
            GetAllResponse(lock_uid = it.uid, lock_name = it.name)
        }

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text, data = res)
    }

    @PostMapping("/open")
    fun open(@RequestBody requestBody: OpenRequest): Response<Nothing> {
        if (!validateCredentials(requestBody.credentials, userService, admin = false))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text)
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
        val todo: Nothing
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

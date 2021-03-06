package com.isp.server.controllers

import com.isp.server.models.*
import com.isp.server.services.LockService
import com.isp.server.services.NextIdService
import com.isp.server.services.UserService
import com.isp.server.util.generatePassword
import com.isp.server.util.hashUserPassword
import com.isp.server.util.validateCredentials
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("api/admin")
class AdminController (private val userService: UserService, private val lockService: LockService, private val nextIdService : NextIdService) {
    val allowedPrivileges = arrayOf("USER", "ADMIN")

    @PostMapping("user/all")
    fun getAllUsers(@RequestBody requestBody: GetAllUsersRequest): Response<List<UserModel>> {
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text, data = userService.getAll())
    }

    @PostMapping("user/add")
    fun createUser(@RequestBody requestBody: AddUserRequest): Response<UserModel> {
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val newUser = UserModel(
            nextIdService.getNextSequence("idSequences"),
            generatePassword(),
            requestBody.name,
            requestBody.surname,
            requestBody.privileges.toUpperCase(),
            // TODO parse from request or leave as empty by default
            mutableListOf()
        )
        userService.insert(hashUserPassword(newUser))
        return Response(status = "OK", message = ResponseMessages.SUCCESS.text, data = newUser)
    }

    @PostMapping("/user/delete")
    fun deleteUser(@RequestBody requestBody: DeleteUserRequest): Response<Nothing> {
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val deletedUser: Optional<UserModel> = userService.deleteById(requestBody.target_user_uid)
        if (deletedUser.isEmpty)
            return Response(status = "DENIED", message = ResponseMessages.USER_NOT_FOUND.text)

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text)
    }

    @PostMapping("/user/update")
    fun updateUser(@RequestBody requestBody: UpdateUserRequest): Response<UserModel> {
        if (!requestBody.reset_password && requestBody.new_privileges == null)
            return Response(status = "DENIED", message = ResponseMessages.NOTHING_TO_UPDATE.text)
        if (requestBody.new_privileges != null && !validatePrivileges(requestBody.new_privileges))
            return Response(status = "DENIED", message = ResponseMessages.WRONG_PRIVILEGES.text)
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val userToUpdate: Optional<UserModel> = userService.getById(requestBody.target_user_uid)
        if (userToUpdate.isEmpty)
            return Response(status = "DENIED", message = ResponseMessages.USER_NOT_FOUND.text)

        var updatedUser: UserModel = userToUpdate.get()
        var updatedUserToSave: UserModel = updatedUser.copy()

        if (requestBody.reset_password) {
            updatedUser = updatedUser.copy(password = generatePassword())
            updatedUserToSave = hashUserPassword(updatedUser)
        }
        if (requestBody.new_privileges != null) {
            updatedUser = updatedUserToSave.copy(privileges = requestBody.new_privileges.toUpperCase())
            updatedUserToSave = updatedUser.copy()
        }
        userService.update(updatedUserToSave)
        return Response(status = "OK", message = ResponseMessages.SUCCESS.text, data = updatedUser)
    }

    @PostMapping("/user/lock/add")
    fun addLockToUser(@RequestBody requestBody: AddLockToUserRequest): Response<Nothing> {
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val userToUpdate: Optional<UserModel> = userService.getById(requestBody.target_user_uid)
        if (userToUpdate.isEmpty)
            return Response(status = "DENIED", message = ResponseMessages.USER_NOT_FOUND.text)

        if (userToUpdate.get().lock_uids.contains(requestBody.lock_uid))
            return Response(status = "DENIED", message = ResponseMessages.LOCK_ALREADY_ADDED.text)

        val lockToAdd: Optional<LockModel> = lockService.getById(requestBody.lock_uid)
        if (lockToAdd.isEmpty)
            return Response(status = "DENIED", message = ResponseMessages.LOCK_NOT_FOUND.text)

        val updatedUser: UserModel = userToUpdate.get()
        updatedUser.lock_uids.add(requestBody.lock_uid)
        userService.update(updatedUser)

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text)
    }

    @PostMapping("/user/lock/delete")
    fun deleteLockFromUser(@RequestBody requestBody: DeleteLockFromUserRequest): Response<Nothing> {
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val userToUpdate: Optional<UserModel> = userService.getById(requestBody.target_user_uid)
        if (userToUpdate.isEmpty)
            return Response(status = "DENIED", message = ResponseMessages.USER_NOT_FOUND.text)

        if (!userToUpdate.get().lock_uids.contains(requestBody.lock_uid))
            return Response(status = "DENIED", message = ResponseMessages.NO_LOCK_FOR_GIVEN_USER.text)

        val updatedUser: UserModel = userToUpdate.get()
        updatedUser.lock_uids.remove(requestBody.lock_uid)
        userService.update(updatedUser)

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text)
    }

    @PostMapping("/lock/all")
    fun getAllLocks(@RequestBody requestBody: GetAllLocksRequest): Response<List<LockModel>> {
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text, data = lockService.getAll())
    }

    @PostMapping("/user/lock/all")
    fun getAllLocksByUser(@RequestBody requestBody: GetAllLocksByUserRequest): Response<List<LockModel>> {
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val targetUser: Optional<UserModel> = userService.getById(requestBody.target_user_uid)
        val lockUids: MutableList<Int> = targetUser.get().lock_uids

        val locks: MutableList<LockModel> = mutableListOf()
        for (lockUid: Int in lockUids) {
            locks.add(lockService.getById(lockUid).get())
        }

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text, data = locks)
    }

    @PostMapping("/lock/rename")
    fun renameLock(@RequestBody requestBody: RenameLockRequest): Response<Nothing> {
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val lockToUpdate: Optional<LockModel> = lockService.getById(requestBody.lock_uid)
        if (lockToUpdate.isEmpty)
            return Response(status = "DENIED", message = ResponseMessages.LOCK_NOT_FOUND.text)

        val updatedLock: LockModel = lockToUpdate.get()
        updatedLock.name = requestBody.lock_new_name
        lockService.update(updatedLock)

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text)
    }

    @PostMapping("/lock/delete")
    fun deleteLock(@RequestBody requestBody: DeleteLockRequest): Response<Nothing> {
        if (!validateCredentials(requestBody.credentials, userService, admin = true))
            return Response(status = "DENIED", message = ResponseMessages.CREDENTIALS_VALIDATION_ERROR.text)

        val deletedLock: Optional<LockModel> = lockService.deleteById(requestBody.lock_uid)
        if (deletedLock.isEmpty)
            return Response(status = "DENIED", message = ResponseMessages.LOCK_NOT_FOUND.text)

        return Response(status = "OK", message = ResponseMessages.SUCCESS.text)
    }

    fun validatePrivileges(privileges: String): Boolean{
        return privileges.toUpperCase() in allowedPrivileges
    }

    data class GetAllUsersRequest(
            val credentials: Credentials,
            val page: Int = 10,
            val size: Int = 10
    )

    data class AddUserRequest(
            val credentials: Credentials,
            val name: String,
            val surname: String,
            val privileges: String
    )

    data class DeleteUserRequest(
            val credentials: Credentials,
            val target_user_uid: Int
    )

    data class UpdateUserRequest(
            val credentials: Credentials,
            val target_user_uid: Int,
            val reset_password: Boolean,
            val new_privileges: String? = null
    )

    data class AddLockToUserRequest(
        val credentials: Credentials,
        val lock_uid: Int,
        val target_user_uid: Int
    )

    data class DeleteLockFromUserRequest(
        val credentials: Credentials,
        val lock_uid: Int,
        val target_user_uid: Int
    )

    data class RenameLockRequest(
        val credentials: Credentials,
        val lock_uid: Int,
        val lock_new_name: String
    )

    data class DeleteLockRequest(
        val credentials: Credentials,
        val lock_uid: Int
    )

    data class GetAllLocksRequest(
        val credentials: Credentials
    )

    data class GetAllLocksByUserRequest(
        val credentials: Credentials,
        val target_user_uid: Int
    )

}

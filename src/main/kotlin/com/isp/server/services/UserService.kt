package com.isp.server.services

import com.isp.server.models.UserModel
import com.isp.server.util.BasicCrud
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(val userDAO: UserDAO) : BasicCrud<Int, UserModel> {

        override fun getAll(): List<UserModel> = userDAO.findAll()

        override fun getAll(pageable: Pageable): Page<UserModel> = userDAO.findAll(pageable)

        override fun getById(id: Int): Optional<UserModel> = userDAO.findById(id)

        override fun insert(obj: UserModel): UserModel = userDAO.insert(obj)

        override fun deleteById(id: Int): Optional<UserModel> {
                return userDAO.findById(id).apply {
                        this.ifPresent {
                                userDAO.delete(it)
                        }
                }
        }

        override fun update(obj: UserModel): UserModel {
                TODO("Not yet implemented")
        }
}

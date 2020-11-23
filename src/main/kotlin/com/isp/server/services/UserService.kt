package com.isp.server.services

import com.isp.server.entites.UserEntity
import com.isp.server.util.BasicCrud
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(val userDAO: UserDAO) : BasicCrud<Int, UserEntity> {

        override fun getAll(pageable: Pageable): Page<UserEntity> = userDAO.findAll(pageable)

        override fun getById(id: Int): Optional<UserEntity> = userDAO.findById(id)

        override fun insert(obj: UserEntity): UserEntity = userDAO.insert(obj)

        override fun deleteById(id: Int): Optional<UserEntity> {
                return userDAO.findById(id).apply {
                        this.ifPresent {
                                userDAO.delete(it)
                        }
                }
        }

        override fun update(obj: UserEntity): UserEntity {
                TODO("Not yet implemented")
        }
}

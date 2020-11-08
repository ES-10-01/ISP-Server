package com.isp.server.services

import com.isp.server.models.User
import com.isp.server.util.BasicCrud
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(val userDAO: UserDAO): BasicCrud<String, User> {

    override fun getAll(pageable: Pageable): Page<User> = userDAO.findAll(pageable)

    override fun getById(id: String): Optional<User> = userDAO.findById(id)

    override fun insert(obj: User): User = userDAO.insert(obj)

    override fun deleteById(id: String): Optional<User> {
        return userDAO.findById(id).apply {
            this.ifPresent {
                userDAO.delete(it)
            }
        }
    }

    override fun update(obj: User): User {
        TODO("Not yet implemented")
    }
}

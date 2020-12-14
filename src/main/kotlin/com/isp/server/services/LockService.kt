package com.isp.server.services

import com.isp.server.models.LockModel
import com.isp.server.util.BasicCrud
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class LockService(val lockDAO: LockDAO) : BasicCrud<Int, LockModel> {

    override fun getAll(): List<LockModel> = lockDAO.findAll()

    override fun getAll(pageable: Pageable): Page<LockModel> = lockDAO.findAll(pageable)

    override fun getById(id: Int): Optional<LockModel> = lockDAO.findById(id)

    override fun insert(obj: LockModel): LockModel = lockDAO.insert(obj)

    override fun deleteById(id: Int): Optional<LockModel> {
        return lockDAO.findById(id).apply {
            this.ifPresent {
                lockDAO.delete(it)
            }
        }
    }

    override fun update(obj: LockModel): LockModel = lockDAO.save(obj)
}
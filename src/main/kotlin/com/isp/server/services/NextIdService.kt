package com.isp.server.services

import com.isp.server.models.IdSeqModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service


@Service
class NextIdService {
    @Autowired
    private val mongo: MongoOperations? = null

    fun getNextSequence(seqName: String?): Int {
        val counter: IdSeqModel = mongo!!.findAndModify(
            Query.query(Criteria.where("_id").`is`(seqName)),
            Update().inc("seq", 1),
            FindAndModifyOptions.options().returnNew(true).upsert(true),
            IdSeqModel::class.java)!!

        return counter.seq
    }
}
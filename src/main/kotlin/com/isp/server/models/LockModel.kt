package com.isp.server.models

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "lock")
data class LockModel (
    @Id
    val uid: Int,
    var name: String = "no_name",
    var TCPConnId: String,
    var ip: String
)

enum class LockStatuses(val text: String) {
    PENDING("Waiting for PIN & verifying"),
    OPENED("Lock has been successfully opened"),
    BLOCKED("Invalid PIN, try again")
}

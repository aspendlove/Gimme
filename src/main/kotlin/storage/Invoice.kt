package storage

import java.sql.Date

data class Invoice(
    val sendDate: Date,
    val status: String,
    val sender: String,
    val clientBusinessName: String,
    val clientEmail: String,
    val clientPhone: String,
    override val id: Int = -1
): hasId

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
): hasId {
    val name: String
    init {
        val iteration = id.toString()
        name = "INV-" + iteration.padStart(8 - iteration.length, '0')
    }
}

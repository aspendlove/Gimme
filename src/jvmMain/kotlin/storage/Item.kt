package storage

import java.math.BigDecimal
import java.sql.Date

data class Item(
    val name: String,
    val startDate: Date,
    val endDate: Date,
    val quantity: Double,
    val price: BigDecimal,
    val description: String,
    val id: Int = -1
)

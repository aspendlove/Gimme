package storage

import java.math.BigDecimal
import java.math.RoundingMode
import java.sql.Date

data class Item(
    val name: String,
    val startDate: Date,
    val endDate: Date?,
    val quantity: Double,
    val price: BigDecimal,
    val description: String,
    val id: Int = -1
) {
    private var total: BigDecimal = BigDecimal("0.0")

    init {
        total = (price * BigDecimal(quantity)).setScale(2, RoundingMode.HALF_EVEN)
    }
}

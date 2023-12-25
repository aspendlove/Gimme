package storage

import java.math.BigDecimal
import java.math.RoundingMode
import java.sql.Date

data class Item(
    val name: String,
    val startDate: Date,
    val endDate: Date?,
    val quantity: Double,
    private val _price: BigDecimal,
    val description: String,
    override val id: Int = -1
): hasId {
    var total: BigDecimal = BigDecimal("0.0")
    val price: BigDecimal
        get() = _price.setScale(2, RoundingMode.HALF_EVEN)

    init {
        total = (price * BigDecimal(quantity)).setScale(2, RoundingMode.HALF_EVEN)
    }
}

package storage

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val startDate: String,
    val endDate: String,
    val item: String,
    val description: String,
    val quantity: Double,
    val price: Double
) {
    private var total: Double = 0.0

    init {
        total = (price * quantity).toBigDecimal().setScale(2).toDouble()
    }
}

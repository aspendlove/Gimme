package storage

import java.math.BigDecimal
import java.math.RoundingMode


object StateBundle {

    lateinit var user: User
    lateinit var client: Client
    lateinit var items: MutableList<Item>
    lateinit var notes: Note
    lateinit var templateName: String

    val total: BigDecimal
        get() {
            return items.fold(BigDecimal(0)) { running, item ->
                running + item.total
            }.setScale(2, RoundingMode.HALF_EVEN)
        }

    fun clear() {
        user = User(
            "", "", "", "", "", "", -1, "", ""
        )
        client = Client(
            "",
            "",
            "",
            "",
            "",
            -1,
            "",
            "",
        )
        items = mutableListOf()
        notes = Note("")
        templateName = ""
    }

    init {
        clear()
    }
}

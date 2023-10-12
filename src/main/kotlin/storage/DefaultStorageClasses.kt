package storage

import java.sql.Date

fun defaultUser(): User {
    return User(
        "",
        "",
        "",
        "",
        "",
        "",
        -1,
        "",
        ""
    )
}

fun defaultClient(): Client {
    return Client(
        "",
        "",
        "",
        "",
        "",
        -1,
        "",
        "",
    )
}

fun defaultItem(): Item {
    return Item(
        "",
        Date(0),
        Date(0),
        -1.0,
        (-1).toBigDecimal(),
        "",
    )
}

fun defaultInvoice(): Invoice {
    return Invoice(
        Date(0),
        "",
        "",
        "",
        "",
        ""
    )
}
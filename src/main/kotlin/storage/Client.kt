package storage

data class Client(
    val businessName: String,
    val contactName: String,
    val street: String,
    val city: String,
    val state: String,
    val zip: Int,
    val email: String,
    val phone: String,
    val id: Int = -1
)

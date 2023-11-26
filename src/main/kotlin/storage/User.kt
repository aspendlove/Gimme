package storage


data class User(
    val businessName: String,
    val contactName: String,
    val subtitle: String,
    val street: String,
    val city: String,
    val state: String,
    val zip: Int,
    val email: String,
    val phone: String,
    val id: Int = -1
)

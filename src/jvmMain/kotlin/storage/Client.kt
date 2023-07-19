package storage

import kotlinx.serialization.Serializable

@Serializable
data class Client(
    var name: String,
    var street: String,
    var city: String,
    var state: String,
    var zip: String,
    var email: String,
    var phone: String
)

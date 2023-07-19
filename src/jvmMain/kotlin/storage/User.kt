package storage

import kotlinx.serialization.Serializable

@Serializable
data class User(
    var businessName:String,
    var name: String,
    var description: String,
    var street: String,
    var city: String,
    var state: String,
    var zip: String,
    var email: String,
    var phone: String
)

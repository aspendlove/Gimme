import storage.Client
import storage.Item
import storage.User

data class SelectionState(
    val user: User = User(
        "default",
        "default",
        "default",
        "default",
        "default",
        "default",
        "default",
        "default",
        "default"
    ),
    val client: Client = Client(
        "default",
        "default",
        "default",
        "default",
        "default",
        "default",
        "default",
    ),
    val items: MutableCollection<Item> = mutableListOf()
)
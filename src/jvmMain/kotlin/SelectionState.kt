import storage.TempClient
import storage.TempItem
import storage.TempUser

data class SelectionState(
    val tempUser: TempUser = TempUser(
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
    val tempClient: TempClient = TempClient(
        "default",
        "default",
        "default",
        "default",
        "default",
        "default",
        "default",
    ),
    val tempItems: MutableCollection<TempItem> = mutableListOf()
)

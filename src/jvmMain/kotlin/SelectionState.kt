import storage.Client
import storage.Item
import storage.User

data class SelectionState(
    val user: User?,
    val client: Client?,
    val items: MutableCollection<Item>?
) {
    fun overwrite(original: SelectionState): SelectionState {
        val toUseUser: User
        val toUseClient: Client
        val toUseItems: MutableCollection<Item>

        if(this.user != null) {
            toUseUser = this.user
        } else {
//            toUseUser =
        }
        return SelectionState(null, null, null)
    }
}
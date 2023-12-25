package storage


object StateBundle {

    lateinit var user: User
    lateinit var client: Client
    lateinit var items: MutableList<Item>
    lateinit var notes: Note

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
    }

    init {
        clear()
    }
}

package storage


object StateBundle {
    var user: User = User(
        "", "", "", "", "", "", -1, "", ""
    )
    var client: Client = Client(
        "",
        "",
        "",
        "",
        "",
        -1,
        "",
        "",
    )
    var items: MutableList<Item> = mutableListOf()
    var notes: Notes = Notes("")
}

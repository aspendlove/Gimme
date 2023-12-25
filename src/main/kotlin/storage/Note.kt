package storage

data class Note(val note: String, override val id: Int = -1) : hasId

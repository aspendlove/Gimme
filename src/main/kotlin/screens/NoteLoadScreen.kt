package screens

import cafe.adriel.voyager.navigator.Navigator
import layouts.LoadScreenItem
import storage.*

class NoteLoadScreen: LoadScreen<Note>() {
    override fun loadRows(navigator: Navigator, filter: String) {
        _rows.clear()

        (if (filter.isEmpty()) DatabaseManager.selectAllNotes() else DatabaseManager.searchNotes(
            NoteColumns.NOTE,
            filter
        )).map { note ->
            val name = if(note.note.length > 10) {
                note.note.substring(0, 10) + "..."
            } else {
                note.note
            }
            _rows.add(LoadScreenItem(iteration++, name, note, { chosenNote ->
                StateBundle.notes = chosenNote
                goToPreviousScreen(navigator)
            }, {
                DatabaseManager.deleteNote(note.id)
                loadRows(navigator, filter)
            }))
        }
    }

    override fun goToPreviousScreen(navigator: Navigator) {
        navigator.replace(NoteScreen())
    }
}

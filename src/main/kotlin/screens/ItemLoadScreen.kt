package screens

import cafe.adriel.voyager.navigator.Navigator
import layouts.LoadScreenItem
import storage.DatabaseManager
import storage.Item
import storage.ItemColumns

class ItemLoadScreen(val addRow: (Item) -> Unit): LoadScreen<Item>() {
    override fun loadRows(navigator: Navigator, filter: String) {
        _rows.clear()

        (if (filter.isEmpty()) DatabaseManager.selectAllItems() else DatabaseManager.searchItems(
            ItemColumns.NAME,
            filter
        )).map { item ->
            _rows.add(LoadScreenItem(iteration++, item.name, item, {
                addRow(item)
                goToPreviousScreen(navigator)
            }, {
                DatabaseManager.deleteItem(item.id)
                loadRows(navigator, filter)
            }))
        }
    }

    override fun goToPreviousScreen(navigator: Navigator) {
        navigator.pop()
    }
}

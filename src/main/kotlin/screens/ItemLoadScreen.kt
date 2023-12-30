package screens

import cafe.adriel.voyager.navigator.Navigator
import layouts.LoadScreenItem
import storage.DatabaseManager
import storage.Item
import storage.ItemColumns
import storage.StateBundle

class ItemLoadScreen: LoadScreen<Item>() {
    override fun loadRows(navigator: Navigator, filter: String) {
        _rows.clear()

        (if (filter.isEmpty()) DatabaseManager.selectAllItems() else DatabaseManager.searchItems(
            ItemColumns.NAME,
            filter
        )).map { item ->
            _rows.add(LoadScreenItem(iteration++, item.name, item, {
                StateBundle.items.add(item)
                goToPreviousScreen(navigator)
            }, {
                DatabaseManager.deleteItem(item.id)
                loadRows(navigator, filter)
            }))
        }
    }

    override fun goToPreviousScreen(navigator: Navigator) {
        navigator.replace(ItemCreationScreen())
    }
}

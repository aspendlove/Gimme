package screens

import cafe.adriel.voyager.navigator.Navigator
import layouts.LoadScreenItem
import storage.*

class ItemLoadScreen: LoadScreen<Item>() {
    override fun loadRows(navigator: Navigator, filter: String) {
        _rows.clear()

        (if (filter.isEmpty()) DatabaseManager.selectAllItems() else DatabaseManager.searchItems(
            ItemColumns.NAME,
            filter
        )).map { item ->
            _rows.add(LoadScreenItem(iteration++, item.name, item, { chosenItem ->
                StateBundle.items.add(item)
                goToPreviousScreen(navigator)
            }, {
                DatabaseManager.deleteUser(item.id)
                loadRows(navigator, filter)
            }))
        }
    }

    override fun goToPreviousScreen(navigator: Navigator) {
        navigator.replace(ItemCreationScreen())
    }
}

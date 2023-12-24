package screens

import cafe.adriel.voyager.navigator.Navigator
import layouts.LoadScreenItem
import storage.Client
import storage.ClientColumns
import storage.DatabaseManager
import storage.DatabaseManager.searchClients
import storage.DatabaseManager.selectAllClients
import storage.StateBundle

class ClientLoadScreen() : LoadScreen<Client>() {
    override fun loadRows(navigator: Navigator, filter: String) {
        _rows.clear()

        (if (filter.isEmpty()) selectAllClients() else searchClients(
            ClientColumns.BUSINESS_NAME,
            filter
        )).map { client ->
            _rows.add(LoadScreenItem(iteration++, client.businessName, client, { chosenClient ->
                StateBundle.client = chosenClient
                navigator.replace(ClientCreationScreen())
            }, {
                DatabaseManager.deleteClient(client.id)
                loadRows(navigator, filter)
            }))
        }
    }

    override fun goToPreviousScreen(navigator: Navigator) {
        navigator.replace(ClientCreationScreen())
    }
}

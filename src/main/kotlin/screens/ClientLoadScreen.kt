package screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import layouts.LoadScreenItem
import storage.Client
import storage.DatabaseManager.selectAllClients
import storage.DatabaseManager.selectAllUsers
import storage.StateBundle
import storage.StateBundle.user
import storage.User

class ClientLoadScreen() : LoadScreen<Client>() {
    @Composable
    override fun loadRows(): MutableList<LoadScreenItem<Client>> {
        val navigator = LocalNavigator.currentOrThrow
        return selectAllClients().map { client ->
            LoadScreenItem(iteration++, client.businessName, client) { chosenClient ->
                StateBundle.client = chosenClient
                navigator.replace(ClientCreationScreen())
            }
        }.toMutableList()
    }
}

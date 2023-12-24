package screens

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import layouts.LoadScreenItem
import storage.DatabaseManager.selectAllUsers
import storage.StateBundle
import storage.User

class UserLoadScreen() : LoadScreen<User>() {
    @Composable
    override fun loadRows(): MutableList<LoadScreenItem<User>> {
        val navigator = LocalNavigator.currentOrThrow
        return selectAllUsers().map { user ->
            LoadScreenItem(iteration++, user.businessName, user) { chosenUser ->
                StateBundle.user = chosenUser
                navigator.replace(UserCreationScreen())
            }
        }.toMutableList()
    }
}

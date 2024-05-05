package screens

import cafe.adriel.voyager.navigator.Navigator
import layouts.LoadScreenItem
import storage.DatabaseManager
import storage.StateBundle
import storage.User
import storage.UserColumns

class UserLoadScreen : LoadScreen<User>() {
    override fun loadRows(navigator: Navigator, filter: String) {
        _rows.clear()

        (if (filter.isEmpty()) DatabaseManager.selectAllUsers() else DatabaseManager.searchUsers(
            UserColumns.BUSINESS_NAME,
            filter
        )).map { user ->
            _rows.add(LoadScreenItem(iteration++, user.businessName, user, { chosenUser ->
                StateBundle.user = chosenUser
                goToPreviousScreen(navigator)
            }, {
                DatabaseManager.deleteUser(user.id)
                loadRows(navigator, filter)
            }))
        }
    }

    override fun goToPreviousScreen(navigator: Navigator) {
        navigator.replace(UserCreationScreen())
    }
}

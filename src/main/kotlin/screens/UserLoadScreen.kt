package screens

import cafe.adriel.voyager.navigator.Navigator
import layouts.LoadScreenItem
import storage.*
import storage.DatabaseManager.selectAllUsers

class UserLoadScreen() : LoadScreen<User>() {
    override fun loadRows(navigator: Navigator, filter: String) {
        _rows.clear()

        (if (filter.isEmpty()) selectAllUsers() else DatabaseManager.searchUsers(
            UserColumns.BUSINESS_NAME,
            filter
        )).map { user ->
            _rows.add(LoadScreenItem(iteration++, user.businessName, user, { chosenUser ->
                StateBundle.user = chosenUser
                navigator.replace(UserCreationScreen())
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

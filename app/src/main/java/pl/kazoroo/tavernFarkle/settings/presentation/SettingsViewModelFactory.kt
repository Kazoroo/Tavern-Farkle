package pl.kazoroo.tavernFarkle.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository

@Suppress("UNCHECKED_CAST")
class SettingsViewModelFactory(
    private val userDataRepository: UserDataRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {

            return SettingsViewModel(userDataRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

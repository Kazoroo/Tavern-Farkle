package pl.kazoroo.tavernFarkle.game.presentation.mainmenu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey
import pl.kazoroo.tavernFarkle.core.domain.ReadUserDataUseCase
import pl.kazoroo.tavernFarkle.core.domain.SaveUserDataUseCase

class MainMenuViewModel(
    private val saveUserDataUseCase: SaveUserDataUseCase,
    readUserDataUseCase: ReadUserDataUseCase
): ViewModel() {
    private val _onboardingStage = MutableStateFlow(RevealableKeys.SpeedDialMenu.ordinal)
    val onboardingStage: StateFlow<Int> = _onboardingStage.asStateFlow()

    private val _isFirstLaunch = MutableStateFlow<Boolean>(readUserDataUseCase(UserDataKey.IS_FIRST_LAUNCH))
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch.asStateFlow()

    fun nextOnboardingStage() {
        _onboardingStage.value++
    }

    fun finishOnboarding() {
        _isFirstLaunch.value = false

        viewModelScope.launch {
            saveUserDataUseCase(false, UserDataKey.IS_FIRST_LAUNCH)
        }
    }
}
package pl.kazoroo.tavernFarkle.game.presentation.mainmenu

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainMenuViewModel(): ViewModel() {
    private val _onboardingStage = mutableIntStateOf(0)
    val onboardingStage: State<Int> = _onboardingStage

    private val _isFirstLaunch = mutableStateOf<Boolean>(true)
    val isFirstLaunch: State<Boolean> = _isFirstLaunch

    fun nextOnboardingStage() {
        _onboardingStage.intValue++

        if(_onboardingStage.intValue == 3) {
            _isFirstLaunch.value = false
        }
    }
}
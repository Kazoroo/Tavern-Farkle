package pl.kazoroo.tavernFarkle.menu.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey
import pl.kazoroo.tavernFarkle.core.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.DrawDiceUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.StartNewGameUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.userdata.ReadUserDataUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.userdata.SaveUserDataUseCase
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

class MainMenuViewModel(
    private val saveUserDataUseCase: SaveUserDataUseCase,
    readUserDataUseCase: ReadUserDataUseCase,
    private val gameRepository: GameRepository,
    private val drawDiceUseCase: DrawDiceUseCase
): ViewModel() {
    private val _onboardingStage = MutableStateFlow(RevealableKeys.Welcome.ordinal)
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

    fun startNewGame(betAmount: Int, userSpecialDiceNames: List<SpecialDiceName>) {
        StartNewGameUseCase(gameRepository, drawDiceUseCase)
            .invoke(
                betAmount,
                userSpecialDiceNames,
                isMultiplayer = false
            )
    }
}
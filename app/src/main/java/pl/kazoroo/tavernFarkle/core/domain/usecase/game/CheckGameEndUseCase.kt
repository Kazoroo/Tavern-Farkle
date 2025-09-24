package pl.kazoroo.tavernFarkle.core.domain.usecase.game

import android.util.Log
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.core.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.core.presentation.navigation.Screen
import pl.kazoroo.tavernFarkle.menu.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.menu.sound.SoundType

class CheckGameEndUseCase(
    private var navController: NavHostController?
) {
    operator fun invoke(
        repository: GameRepository,
        sumCoins: () -> Unit = {},
    ): Boolean {
        if(repository.gameState.value.players[repository.gameState.value.getCurrentPlayerIndex()].totalPoints >= 4000) {
            CoroutineScope(Dispatchers.Main).launch {
                if (repository.gameState.value.currentPlayerUuid == repository.myUuidState.value) {
                    SoundPlayer.playSound(SoundType.WIN)
                    sumCoins()
                } else {
                    SoundPlayer.playSound(SoundType.FAILURE)
                }
                repository.toggleGameEnd()
                delay(3000L)
                repository.toggleGameEnd()

                navController?.let { navController ->
                    navController.navigate(Screen.MainScreen.withArgs()) {
                        popUpTo(Screen.GameScreen.withArgs()) { inclusive = true }
                    }
                } ?: Log.e("CheckGameEndUseCase", "navController in CheckGameEndUseCase is null")
            }

            return true
        }

        return false
    }

    fun initializeNavController(navController: NavHostController) {
        this.navController = navController
    }
}
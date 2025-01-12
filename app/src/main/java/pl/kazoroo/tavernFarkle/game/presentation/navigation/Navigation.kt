package pl.kazoroo.tavernFarkle.game.presentation.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.kazoroo.tavernFarkle.core.data.local.UserDataRepository
import pl.kazoroo.tavernFarkle.core.domain.ReadUserDataUseCase
import pl.kazoroo.tavernFarkle.core.domain.SaveUserDataUseCase
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.game.presentation.game.GameScreen
import pl.kazoroo.tavernFarkle.game.presentation.mainmenu.MainMenuScreen
import pl.kazoroo.tavernFarkle.shop.presentation.ShopScreen

@ExperimentalMaterial3Api
@Composable
fun Navigation(
    userDataRepository: UserDataRepository
) {
    val navController = rememberNavController()
    val saveUserDataUseCase = SaveUserDataUseCase(userDataRepository)
    val readUserDataUseCase = ReadUserDataUseCase(userDataRepository)

    val coinsViewModel = CoinsViewModel(
        saveUserDataUseCase,
        readUserDataUseCase
    )

    NavHost(
        navController = navController,
        startDestination = Screen.MainScreen.route
    ) {
        composable(
            route = Screen.MainScreen.route
        ) {
            MainMenuScreen(
                navController = navController,
                coinsViewModel = coinsViewModel
            )
        }
        composable(
            route = Screen.GameScreen.route
        ) {
            GameScreen(
                bettingActions = coinsViewModel,
                navController = navController
            )
        }
        composable(
            route = Screen.ShopScreen.route
        ) {
            ShopScreen(
                coinsViewModel = coinsViewModel,
                saveUserDataUseCase = saveUserDataUseCase,
                readUserDataUseCase = readUserDataUseCase,
            )
        }
    }
}
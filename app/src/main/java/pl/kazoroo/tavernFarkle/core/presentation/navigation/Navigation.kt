package pl.kazoroo.tavernFarkle.core.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.svenjacobs.reveal.RevealCanvas
import com.svenjacobs.reveal.rememberRevealCanvasState
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.di.DependencyContainer
import pl.kazoroo.tavernFarkle.menu.presentation.MainMenuScreen
import pl.kazoroo.tavernFarkle.menu.presentation.MainMenuViewModel
import pl.kazoroo.tavernFarkle.multiplayer.presentation.LobbyScreen
import pl.kazoroo.tavernFarkle.multiplayer.presentation.LobbyViewModel
import pl.kazoroo.tavernFarkle.settings.presentation.SettingsScreen
import pl.kazoroo.tavernFarkle.settings.presentation.SettingsViewModel
import pl.kazoroo.tavernFarkle.shop.domain.usecase.BuySpecialDiceUseCase
import pl.kazoroo.tavernFarkle.shop.presentation.inventory.InventoryScreen
import pl.kazoroo.tavernFarkle.shop.presentation.inventory.InventoryViewModel
import pl.kazoroo.tavernFarkle.shop.presentation.shop.ShopScreen
import pl.kazoroo.tavernFarkle.singleplayer.presentation.GameScreen
import pl.kazoroo.tavernFarkle.singleplayer.presentation.GameViewModel

@Composable
fun Navigation(
    dependencyContainer: DependencyContainer,
    mainMenuViewModel: MainMenuViewModel,
    coinsViewModel: CoinsViewModel,
    inventoryViewModel: InventoryViewModel,
    settingsViewModel: SettingsViewModel,
    lobbyViewModel: LobbyViewModel
) {
    val navController = rememberNavController()
    val revealCanvasState = rememberRevealCanvasState()

    RevealCanvas(
        modifier = Modifier.fillMaxSize(),
        revealCanvasState = revealCanvasState,
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.MainScreen.route
        ) {
            composable(
                route = Screen.MainScreen.route
            ) {
                MainMenuScreen(
                    navController = navController,
                    coinsViewModel = coinsViewModel,
                    mainMenuViewModel = mainMenuViewModel,
                    inventoryViewModel = inventoryViewModel,
                    revealCanvasState = revealCanvasState
                )
            }
            composable(
                route = Screen.GameScreen.route + "/{isMultiplayer}",
                arguments = listOf(navArgument("isMultiplayer") {
                    type = NavType.BoolType
                    defaultValue = false
                })
            ) {
                val isMultiplayer = it.arguments?.getBoolean("isMultiplayer") ?: false
                val gameViewModel = viewModel<GameViewModel>(
                    factory = dependencyContainer.gameViewModelFactory(isMultiplayer)
                )

                GameScreen(
                    navController = navController,
                    viewModel = gameViewModel,
                    coinsViewModel = coinsViewModel,
                    revealCanvasState = revealCanvasState
                )
            }
            composable(
                route = Screen.LobbyScreen.route,
            ) {
                LobbyScreen(
                    coinsAmount = coinsViewModel.coinsAmount.collectAsState().value,
                    navController = navController,
                    lobbyViewModel = lobbyViewModel,
                    inventoryViewModel = inventoryViewModel,
                    coinsViewModel = coinsViewModel
                )
            }
            composable(
                route = Screen.ShopScreen.route
            ) {
                ShopScreen(
                    coinsViewModel = coinsViewModel,
                    buySpecialDiceUseCase = BuySpecialDiceUseCase(dependencyContainer.inventoryDataRepository),
                    navController = navController
                )
            }

            composable(
                route = Screen.InventoryScreen.route
            ) {
                InventoryScreen(
                    inventoryViewModel = inventoryViewModel,
                    navController = navController
                )
            }

            composable(
                route = Screen.SettingsScreen.route
            ) {
                SettingsScreen(
                    settingsViewModel,
                    navController = navController
                )
            }
        }
    }
}
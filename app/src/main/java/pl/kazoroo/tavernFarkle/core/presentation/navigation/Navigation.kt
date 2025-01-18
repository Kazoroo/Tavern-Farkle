package pl.kazoroo.tavernFarkle.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository
import pl.kazoroo.tavernFarkle.core.domain.ReadUserDataUseCase
import pl.kazoroo.tavernFarkle.core.domain.SaveUserDataUseCase
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.game.presentation.game.GameScreen
import pl.kazoroo.tavernFarkle.game.presentation.mainmenu.MainMenuScreen
import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice
import pl.kazoroo.tavernFarkle.shop.data.repository.InventoryDataRepository
import pl.kazoroo.tavernFarkle.shop.domain.usecase.BuySpecialDiceUseCase
import pl.kazoroo.tavernFarkle.shop.presentation.inventory.InventoryScreen
import pl.kazoroo.tavernFarkle.shop.presentation.inventory.InventoryViewModel
import pl.kazoroo.tavernFarkle.shop.presentation.shop.ShopScreen

@Composable
fun Navigation(
    userDataRepository: UserDataRepository,
    inventoryDataRepository: InventoryDataRepository,
    protoDataStore: DataStore<List<OwnedSpecialDice>>
) {
    val navController = rememberNavController()
    val saveUserDataUseCase = SaveUserDataUseCase(userDataRepository)
    val readUserDataUseCase = ReadUserDataUseCase(userDataRepository)

    val coinsViewModel = CoinsViewModel(
        saveUserDataUseCase,
        readUserDataUseCase
    )

    val inventoryViewModel = InventoryViewModel(
        dataStore = protoDataStore
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
                buySpecialDiceUseCase = BuySpecialDiceUseCase(inventoryDataRepository)
            )
        }

        composable(
            route = Screen.InventoryScreen.route
        ) {
            InventoryScreen(
                inventoryViewModel = inventoryViewModel
            )
        }
    }
}
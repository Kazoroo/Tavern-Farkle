package pl.kazoroo.dices.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.kazoroo.dices.ui.theme.screens.GameScreen
import pl.kazoroo.dices.ui.theme.screens.MainMenuScreen
import pl.kazoroo.dices.ui.theme.screens.SettingsScreen
import pl.kazoroo.dices.ui.theme.screens.ShopScreen

@ExperimentalMaterial3Api
@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(
                route = Screen.MainScreen.route
        ) {
            MainMenuScreen(navController = navController)
        }
        composable(
                route = Screen.SettingsScreen.route
        ) {
            SettingsScreen(navController = navController)
        }
        composable(
                route = Screen.GameScreen.route
        ) {
            GameScreen(navController = navController)
        }
        composable(
                route = Screen.ShopScreen.route
        ) {
            ShopScreen(navController = navController)
        }
    }
}


//Button(onClick = { navController.navigate(Screen.DetailScreen.withArgs(text)) })
//name = entry.arguments?.getString("name")
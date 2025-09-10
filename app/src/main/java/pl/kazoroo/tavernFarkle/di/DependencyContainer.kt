package pl.kazoroo.tavernFarkle.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository
import pl.kazoroo.tavernFarkle.core.domain.ReadUserDataUseCase
import pl.kazoroo.tavernFarkle.core.domain.SaveUserDataUseCase
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.core.presentation.viewModelFactoryHelper
import pl.kazoroo.tavernFarkle.game.data.repository.LocalGameRepository
import pl.kazoroo.tavernFarkle.game.domain.usecase.CalculatePointsUseCase
import pl.kazoroo.tavernFarkle.game.presentation.game.GameViewModelRefactor
import pl.kazoroo.tavernFarkle.game.presentation.mainmenu.MainMenuViewModel
import pl.kazoroo.tavernFarkle.settings.presentation.SettingsViewModel
import pl.kazoroo.tavernFarkle.shop.domain.InventoryDataRepositoryImpl
import pl.kazoroo.tavernFarkle.shop.presentation.inventory.InventoryViewModel

class DependencyContainer(
    context: Context
) {
    private val appContext = context.applicationContext

    private val userDataRepository by lazy {
        UserDataRepository.getInstance(appContext)
    }
    val inventoryDataRepository by lazy {
        InventoryDataRepositoryImpl.getInstance(appContext)
    }
    val saveUserDataUseCase by lazy {
        SaveUserDataUseCase(userDataRepository)
    }
    val readUserDataUseCase by lazy {
        ReadUserDataUseCase(userDataRepository)
    }
    val calculatePointsUseCase by lazy {
        CalculatePointsUseCase(localGameRepository)
    }
    val localGameRepository by lazy {
        LocalGameRepository()
    }

    val settingsViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactoryHelper {
            SettingsViewModel(
                userDataRepository = userDataRepository
            )
        }
    val inventoryViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactoryHelper {
            InventoryViewModel(
                inventoryDataRepository = inventoryDataRepository
            )
        }
    val coinsViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactoryHelper {
            CoinsViewModel(
                saveUserDataUseCase = saveUserDataUseCase,
                readUserDataUseCase = readUserDataUseCase
            )
        }
    val mainMenuViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactoryHelper {
            MainMenuViewModel(
                saveUserDataUseCase = saveUserDataUseCase,
                readUserDataUseCase = readUserDataUseCase,
                gameRepository = localGameRepository
            )
        }
    val gameViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactoryHelper {
            GameViewModelRefactor(
                repository = localGameRepository,
                calculatePointsUseCase = calculatePointsUseCase
            )
        }
}
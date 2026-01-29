package pl.kazoroo.tavernFarkle.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository
import pl.kazoroo.tavernFarkle.core.domain.GameStateUpdater
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.CalculatePointsUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.CheckForSkuchaUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.DrawDiceUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.StartNewGameUseCaseFactory
import pl.kazoroo.tavernFarkle.core.domain.usecase.userdata.ReadUserDataUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.userdata.SaveUserDataUseCase
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.core.presentation.viewModelFactoryHelper
import pl.kazoroo.tavernFarkle.menu.presentation.MainMenuViewModel
import pl.kazoroo.tavernFarkle.multiplayer.JoinLobbyUseCase
import pl.kazoroo.tavernFarkle.multiplayer.data.remote.FirebaseDataSource
import pl.kazoroo.tavernFarkle.multiplayer.data.repository.RemoteGameRepository
import pl.kazoroo.tavernFarkle.multiplayer.presentation.LobbyViewModel
import pl.kazoroo.tavernFarkle.settings.presentation.SettingsViewModel
import pl.kazoroo.tavernFarkle.shop.domain.InventoryDataRepositoryImpl
import pl.kazoroo.tavernFarkle.shop.domain.protoDataStore
import pl.kazoroo.tavernFarkle.shop.presentation.inventory.InventoryViewModel
import pl.kazoroo.tavernFarkle.singleplayer.data.repository.LocalGameRepository
import pl.kazoroo.tavernFarkle.singleplayer.domain.usecase.PlayOpponentTurnUseCase
import pl.kazoroo.tavernFarkle.singleplayer.presentation.GameViewModel

class DependencyContainer(
    context: Context
) {
    private val appContext = context.applicationContext

    private val userDataRepository by lazy {
        UserDataRepository.getInstance(appContext)
    }
    val inventoryDataRepository by lazy {
        InventoryDataRepositoryImpl(appContext.protoDataStore)
    }
    val saveUserDataUseCase by lazy {
        SaveUserDataUseCase(userDataRepository)
    }
    val readUserDataUseCase by lazy {
        ReadUserDataUseCase(userDataRepository)
    }
    val calculatePointsUseCase by lazy {
        CalculatePointsUseCase()
    }
    val checkForSkuchaUseCase by lazy {
        CheckForSkuchaUseCase(calculatePointsUseCase)
    }
    val drawDiceUseCase by lazy {
        DrawDiceUseCase(checkForSkuchaUseCase, readUserDataUseCase)
    }
    val playOpponentTurnUseCase by lazy {
        PlayOpponentTurnUseCase(localGameRepository, drawDiceUseCase, calculatePointsUseCase)
    }
    val firebaseDataSource by lazy {
        FirebaseDataSource()
    }
    val gameStateUpdater by lazy {
        GameStateUpdater()
    }
    val localGameRepository by lazy {
        LocalGameRepository(gameStateUpdater)
    }
    val remoteGameRepository by lazy {
        RemoteGameRepository(firebaseDataSource, gameStateUpdater)
    }
    val joinLobbyUseCase by lazy {
        JoinLobbyUseCase(remoteGameRepository, drawDiceUseCase, firebaseDataSource)
    }

    val startNewGameUseCaseFactory: StartNewGameUseCaseFactory
        get() = StartNewGameUseCaseFactory(
            localRepo = localGameRepository,
            remoteRepo = remoteGameRepository,
            drawDiceUseCase = drawDiceUseCase
        )

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
                gameRepository = localGameRepository,
                drawDiceUseCase = drawDiceUseCase
            )
        }
    val lobbyViewModelFactory: ViewModelProvider.Factory
        get() = viewModelFactoryHelper {
            LobbyViewModel(
                remoteGameRepository = remoteGameRepository,
                joinLobbyUseCase = joinLobbyUseCase,
                startNewGameUseCaseFactory = startNewGameUseCaseFactory,
                checkForSkuchaUseCase = checkForSkuchaUseCase
            )
        }

    fun gameViewModelFactory(isMultiplayer: Boolean): ViewModelProvider.Factory =
        viewModelFactoryHelper {
            GameViewModel(
                repository = if (isMultiplayer) remoteGameRepository else localGameRepository,
                calculatePointsUseCase = calculatePointsUseCase,
                drawDiceUseCase = drawDiceUseCase,
                playOpponentTurnUseCase = playOpponentTurnUseCase,
                isMultiplayer = isMultiplayer,
                readUserDataUseCase = readUserDataUseCase,
                saveUserDataUseCase = saveUserDataUseCase
            )
        }
}
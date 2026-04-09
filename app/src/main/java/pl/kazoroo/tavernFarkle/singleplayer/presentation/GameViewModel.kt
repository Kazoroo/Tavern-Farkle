package pl.kazoroo.tavernFarkle.singleplayer.presentation

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import pl.kazoroo.tavernFarkle.core.domain.repository.GameRepository
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.CalculatePointsUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.game.DrawDiceUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.userdata.ReadUserDataUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.userdata.SaveUserDataUseCase
import pl.kazoroo.tavernFarkle.menu.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.menu.sound.SoundType
import pl.kazoroo.tavernFarkle.multiplayer.data.UpdatePlayerStatusWorker
import pl.kazoroo.tavernFarkle.multiplayer.data.remote.PlayerStatus
import pl.kazoroo.tavernFarkle.singleplayer.domain.usecase.PlayOpponentTurnUseCase
import pl.kazoroo.tavernFarkle.singleplayer.presentation.components.GameLoopEvent

class GameViewModel(
    private val repository: GameRepository,
    private val calculatePointsUseCase: CalculatePointsUseCase,
    private val drawDiceUseCase: DrawDiceUseCase,
    private val playOpponentTurnUseCase: PlayOpponentTurnUseCase,
    val isMultiplayer: Boolean,
    private val saveUserDataUseCase: SaveUserDataUseCase,
    readUserDataUseCase: ReadUserDataUseCase,
): ViewModel() {
    val gameState: StateFlow<GameState> = repository.gameState

    private val _onboardingStage = MutableStateFlow(GameRevealableKeys.ScoringDice.ordinal)
    val onboardingStage: StateFlow<Int> = _onboardingStage.asStateFlow()

    private val _isFirstLaunch = MutableStateFlow<Boolean>(readUserDataUseCase(UserDataKey.IS_FIRST_GAME))
    val isFirstLaunch: StateFlow<Boolean> = _isFirstLaunch.asStateFlow()

    val isOpponentTurn: StateFlow<Boolean> =
        gameState.map { state ->
            state.currentPlayerUuid != repository.myUuidState.value
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000L, 0), false)

    private val _isDiceAnimating = MutableStateFlow(true)
    val isDiceAnimating: StateFlow<Boolean> = _isDiceAnimating

    private val _showGameEndDialog = MutableStateFlow(false)
    val showGameEndDialog: StateFlow<Boolean> = _showGameEndDialog

    private val _showSkuchaDialog = MutableStateFlow(false)
    val showSkuchaDialog: StateFlow<Boolean> = _showSkuchaDialog

    val myPlayerIndex = repository.getMyPlayerIndex()
    val opponentPlayerIndex: StateFlow<Int?> =
        repository.getOpponentPlayerIndex()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(1000L), null)

    var playerQuit by mutableStateOf(false)
        private set

    var timerValue by mutableIntStateOf(-1)
        private set

    private val _effects = MutableSharedFlow<GameLoopEvent>(extraBufferCapacity = 8)
    val effects: SharedFlow<GameLoopEvent> = _effects.asSharedFlow()

    private val gameLoopChannel = Channel<GameLoopEvent>(Channel.UNLIMITED)

    init {
        observeSkucha()
        observeDiceAnimation()
        processGameLoop()
        observeGameEnd()
    }

    fun finishOnboarding() {
        _isFirstLaunch.value = false

        viewModelScope.launch {
            saveUserDataUseCase(false, UserDataKey.IS_FIRST_GAME)
        }
    }

    fun nextOnboardingStage() {
        _onboardingStage.value++
    }

    private fun processGameLoop() {
        viewModelScope.launch {
            for (event in gameLoopChannel) {
                when (event) {
                    GameLoopEvent.Pass -> handlePass()
                    GameLoopEvent.ScoreAndRoll -> handleScoreAndRoll()
                    GameLoopEvent.Skucha -> handleSkucha()
                    is GameLoopEvent.GameEnd -> handleGameEnd()
                }
            }
        }
    }

    fun observeGameEnd() {
        viewModelScope.launch {
            repository.gameState
                .filter { it.isGameEnd }
                .distinctUntilChangedBy { it.isGameEnd }
                .collect {
                    gameLoopChannel.send(GameLoopEvent.GameEnd(true))
                }
        }
    }

    suspend fun handleGameEnd() {
        val isWin = repository.gameState.value.currentPlayerUuid == repository.myUuidState.value
        _effects.emit(GameLoopEvent.GameEnd(isWin))

        delay(1000L)
        _showGameEndDialog.value = true

        repository.removeLobbyNode()
    }

    fun checkForGameEnd(): Boolean {
        if(repository.gameState.value.players[repository.gameState.value.getCurrentPlayerIndex()].totalPoints >= 500) {
            repository.setGameEnd(true)

            return true
        }

        return false
    }

    fun toggleDiceSelection(index: Int) {
        repository.toggleDiceSelection(index)
        val currentPlayerDiceSet = gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet
        calculatePointsUseCase(
            diceList = currentPlayerDiceSet,
            repository = repository
        )
    }

    fun onPass() {
        viewModelScope.launch {
            gameLoopChannel.send(GameLoopEvent.Pass)
        }
    }

    fun handlePass() {
        repository.sumTotalPoints()

        if(checkForGameEnd()) return

        viewModelScope.launch {
            repository.toggleDiceRowAnimation()
            delay(600L)
            repository.resetDiceState()
            repository.changeCurrentPlayer()
            drawDiceUseCase(
                repository.gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet,
                repository = repository
            )

            val isOpponentTurn = repository.gameState.value.currentPlayerUuid != repository.myUuidState.value

            if(isOpponentTurn && !isMultiplayer) {
                playOpponentTurnUseCase { checkForGameEnd() }
            }
        }
    }

    fun onScoreAndRoll() {
        viewModelScope.launch {
            gameLoopChannel.send(GameLoopEvent.ScoreAndRoll)
        }
    }

    fun handleScoreAndRoll() {
        repository.sumRoundPoints()

        viewModelScope.launch {
            repository.hideSelectedDice()
            repository.toggleDiceRowAnimation()
            delay(600L)

            if(repository.gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet.all { !it.isVisible }) {
                repository.resetDiceState()
            }

            drawDiceUseCase(
                repository.gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet,
                repository = repository
            )
        }
    }

    fun observeSkucha() {
        viewModelScope.launch {
            repository.gameState
                .map { it.isSkucha }
                .distinctUntilChanged()
                .collect { isSkucha ->
                    if (isSkucha) {
                        gameLoopChannel.send(GameLoopEvent.Skucha)
                    }
                }
        }
    }

    private suspend fun handleSkucha() {
        delay(2000)
        SoundPlayer.playSound(SoundType.SKUCHA)
        _showSkuchaDialog.value = true
        delay(2000)
        _showSkuchaDialog.value = false

        val isHost = gameState.value.players[0].uuid == repository.myUuidState.value

        if (!isHost) return

        repository.setSkucha(false)
        repository.resetRoundAndSelectedPoints()
        repository.toggleDiceRowAnimation()

        delay(600)
        repository.resetDiceState()
        repository.changeCurrentPlayer()

        drawDiceUseCase(
            repository.gameState.value.players[gameState.value.getCurrentPlayerIndex()].diceSet,
            repository = repository
        )

        val isOpponentTurn = repository.gameState.value.currentPlayerUuid != repository.myUuidState.value

        if (isOpponentTurn && !isMultiplayer) {
            playOpponentTurnUseCase { checkForGameEnd() }
        }
    }

    private fun observeDiceAnimation() {
        viewModelScope.launch {
            repository.gameState
                .map { it.isAnimating }
                .distinctUntilChanged()
                .collect {
                    delay(200L)
                    _isDiceAnimating.value = true
                    delay(500L)
                    SoundPlayer.playSound(SoundType.DICE_ROLLING)
                    delay(500L)
                    _isDiceAnimating.value = false
                }
        }
    }

    fun onQuit(takeBet: () -> Unit) {
        if(gameState.value.players.size == 1) {
            repository.removeLobbyNode()
        } else {
            takeBet()
            repository.updatePlayerStatus(
                status = PlayerStatus.LEFT,
                timestamp = System.currentTimeMillis(),
                updateRemotely = isMultiplayer
            )
        }
    }

    fun observePlayerStatus(navController: NavHostController, handleGameEndRewards: () -> Unit) {
        viewModelScope.launch {
            opponentPlayerIndex
                .filterNotNull()
                .distinctUntilChanged()
                .collect { opponentPlayerIndex ->
                    repository.gameState
                        .map { state ->
                            state.players.getOrNull(opponentPlayerIndex)?.status
                        }
                        .filterNotNull()
                        .distinctUntilChanged()
                        .collect {
                            when (it) {
                                PlayerStatus.LEFT -> {
                                    playerQuit = true
                                    handleGameEndRewards()
                                    repository.removeLobbyNode()
                                }
                                PlayerStatus.PAUSED -> {
                                    startTimer(navController, handleGameEndRewards)
                                }

                                PlayerStatus.IN_GAME -> {
                                    timerJob?.cancel()
                                    timerValue = -1
                                }
                            }
                        }
                }
        }
    }

    private var timerJob: Job? = null
    private fun startTimer(navController: NavHostController, handleGameEndRewards: () -> Unit) {
        timerJob?.cancel()

        timerJob = viewModelScope.launch {
            for(i in 30 downTo 0) {
                timerValue = i
                delay(1000L)
            }

            timerValue = -1
            handleGameEndRewards()
            navController.navigateUp()
            repository.removeLobbyNode()
        }
    }

    fun updatePlayerState(status: PlayerStatus, context: Context) {
        val timestamp = System.currentTimeMillis()

        repository.updatePlayerStatus(status, timestamp,  updateRemotely = false)

        val data = Data.Builder()
            .putString("status", status.name)
            .putInt("playerIndex", repository.getMyPlayerIndex())
            .putString("gameUuid", gameState.value.gameUuid.toString())
            .putLong("timestamp", timestamp)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<UpdatePlayerStatusWorker>()
            .setInputData(data)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}

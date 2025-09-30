package pl.kazoroo.tavernFarkle.core.domain

import pl.kazoroo.tavernFarkle.core.domain.model.Dice
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import pl.kazoroo.tavernFarkle.core.domain.model.Player

class GameStateUpdater {

    /**
     * Saves provided data as the current game state. Used to initialize game data.
     * @param newState state to save.
     */
    fun saveGameState(newState: GameState): GameState = newState

    /**
     * Toggles the selection state of a dice at the specified index.
     * @param index The index of the dice to toggle the selection state for.
     */
    fun toggleDiceSelection(state: GameState, index: Int): GameState {
        val currentIndex = state.getCurrentPlayerIndex()
        val updatedPlayers = state.players.toMutableList()
        val updatedDiceSet = updatedPlayers[currentIndex].diceSet.toMutableList()

        updatedDiceSet[index] = updatedDiceSet[index].copy(isSelected = !updatedDiceSet[index].isSelected)
        updatedPlayers[currentIndex] = updatedPlayers[currentIndex].copy(diceSet = updatedDiceSet)

        return state.copy(players = updatedPlayers)
    }

    /**
     * Updates the selected points state for the current player.
     * @param selectedPoints The new selected points value.
     */
    fun updateSelectedPoints(state: GameState, selectedPoints: Int): GameState {
        val currentIndex = state.getCurrentPlayerIndex()
        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[currentIndex] = updatedPlayers[currentIndex].copy(selectedPoints = selectedPoints)

        return state.copy(players = updatedPlayers)
    }

    /**
     * Assign selected and round points to the current player round points state. Reset selected points.
     */
    fun sumRoundPoints(state: GameState): GameState {
        val currentIndex = state.getCurrentPlayerIndex()
        val player = state.players[currentIndex]

        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[currentIndex] = player.copy(
            roundPoints = player.roundPoints + player.selectedPoints,
            selectedPoints = 0
        )

        return state.copy(players = updatedPlayers)
    }

    /**
     * Updates the dice visibility state for the current player based on their selected state.
     */
    fun hideSelectedDice(state: GameState): GameState {
        val currentIndex = state.getCurrentPlayerIndex()
        val updatedDice = state.players[currentIndex].diceSet.map { dice ->
            if (dice.isSelected) dice.copy(isVisible = false, isSelected = false) else dice
        }

        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[currentIndex] = updatedPlayers[currentIndex].copy(diceSet = updatedDice)

        return state.copy(players = updatedPlayers)
    }

    /**
     * Assigns given dice list to the current player dice set state.
     * @param newDice The new dice set to be assigned to the current player state.
     */
    fun updateDiceSet(state: GameState, newDice: List<Dice>): GameState {
        val currentIndex = state.getCurrentPlayerIndex()
        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[currentIndex] = updatedPlayers[currentIndex].copy(diceSet = newDice)

        return state.copy(players = updatedPlayers)
    }

    /**
     * Assign selected, round and total points to the total current player points. Reset selected and round points.
     */
    fun sumTotalPoints(state: GameState): GameState {
        val currentIndex = state.getCurrentPlayerIndex()
        val player = state.players[currentIndex]

        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[currentIndex] = player.copy(
            totalPoints = player.totalPoints + player.roundPoints + player.selectedPoints,
            roundPoints = 0,
            selectedPoints = 0
        )

        return state.copy(players = updatedPlayers)
    }

    /**
     * Resets to default the dice selection and visibility state for the current player.
     */
    fun resetDiceState(state: GameState): GameState {
        val currentIndex = state.getCurrentPlayerIndex()
        val updatedDice = state.players[currentIndex].diceSet.map { dice ->
            dice.copy(isSelected = false, isVisible = true)
        }

        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[currentIndex] = updatedPlayers[currentIndex].copy(diceSet = updatedDice)

        return state.copy(players = updatedPlayers)
    }

    /**
     * Updates the state of current player uuid to the next player.
     */
    fun changeCurrentPlayer(state: GameState): GameState {
        return state.copy(
            currentPlayerUuid = state.players.first { it.uuid != state.currentPlayerUuid }.uuid
        )
    }

    /**
     * Resets the round and selected points for the current player.
     */
    fun resetRoundAndSelectedPoints(state: GameState): GameState {
        val currentIndex = state.getCurrentPlayerIndex()
        val updatedPlayers = state.players.toMutableList()
        updatedPlayers[currentIndex] = updatedPlayers[currentIndex].copy(
            roundPoints = 0,
            selectedPoints = 0
        )

        return state.copy(players = updatedPlayers)
    }

    /**
     * Toggles the skucha state of the game.
     */
    fun toggleSkucha(state: GameState): GameState {
        return state.copy(isSkucha = !state.isSkucha)
    }

    /**
     * Toggles the game end state.
     */
    fun toggleGameEnd(state: GameState): GameState {
        return state.copy(isGameEnd = !state.isGameEnd)
    }

    fun updatePlayers(state: GameState, players: List<Player>): GameState {
        return state.copy(players = players)
    }
}
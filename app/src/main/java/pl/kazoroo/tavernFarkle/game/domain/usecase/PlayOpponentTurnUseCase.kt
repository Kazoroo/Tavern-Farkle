package pl.kazoroo.tavernFarkle.game.domain.usecase

import kotlinx.coroutines.delay
import pl.kazoroo.tavernFarkle.game.domain.repository.GameRepository

class PlayOpponentTurnUseCase(
    private val repository: GameRepository,
    private val drawDiceUseCase: DrawDiceUseCase,
    private val calculatePointsUseCase: CalculatePointsUseCase
) {
    suspend operator fun invoke() {
        val playingUntilDiceLeft = (2..3).random()
        var numberOfVisibleDice = 6

        while(numberOfVisibleDice > playingUntilDiceLeft) {
            delay((1600L..2000L).random())
            val indexesOfDiceGivingPoints = searchForDiceIndexGivingPoints()

            if (indexesOfDiceGivingPoints.isEmpty()) {
                //TODO: perform skucha
                println("performing skucha")

                return
            }

            indexesOfDiceGivingPoints.forEach {
                repository.toggleDiceSelection(it)
                calculatePointsUseCase(repository.gameState.value.players[1].diceSet)
                println("selected dice index $it")
                delay((1200L..1600L).random())
            }

            println("number of visible dice: $numberOfVisibleDice")
            println("selected dice: ${indexesOfDiceGivingPoints.size}")
            println("playing until dice left: $playingUntilDiceLeft")

            if (numberOfVisibleDice - indexesOfDiceGivingPoints.size > playingUntilDiceLeft) {
                scoreAndRollAgain()
                println("rolling again")
                numberOfVisibleDice = repository.gameState.value.players[1].diceSet.count { !it.isVisible }
            } else {
                passRound()
                println("Pass round")
                break
            }

            println(repository.gameState.value.currentPlayerUuid)
            repository.gameState.value.players.forEach { println(it.uuid) }
            println(repository.gameState.value.players[0].totalPoints)
            println(repository.gameState.value.players[1].totalPoints)
        }

        println("ending turn")
    }

    /**
     * @return List of dice indexes that gives points
     */
    private fun searchForDiceIndexGivingPoints(): List<Int> {
        val gameState = repository.gameState.value
        val opponentDiceSet = gameState.players[1].diceSet
        val sequenceDice: List<Int> =
            opponentDiceSet.filterIndexed { index, _ -> opponentDiceSet[index].isVisible }
                .groupingBy { it.value }.eachCount().filter { it.value >= 3 }.keys.toList()

        val indexesOfDiceGivingPoints = opponentDiceSet.mapIndexedNotNull { index, dice ->
            if ((dice.value == 1 || dice.value == 5 || sequenceDice.contains(dice.value)) && opponentDiceSet[index].isVisible) index else null
        }.shuffled()

        return indexesOfDiceGivingPoints
    }

    private fun passRound() {
        val gameState = repository.gameState.value

        repository.sumTotalPoints()
        repository.resetDiceState()
        repository.changeCurrentPlayer()
        drawDiceUseCase(gameState.players[gameState.getCurrentPlayerIndex()].diceSet)
    }

    private fun scoreAndRollAgain() {
        val gameState = repository.gameState.value

        repository.sumRoundPoints()
        repository.hideSelectedDice()
        drawDiceUseCase(gameState.players[gameState.getCurrentPlayerIndex()].diceSet)
    }
}
package pl.kazoroo.tavernFarkle.multiplayer.data.remote

import com.google.firebase.Firebase
import com.google.firebase.database.database
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import java.util.UUID

class FirebaseDataSource {
    val database = Firebase.database

    fun setGameState(gameState: GameState) {
        val ref = database.getReference(gameState.gameUuid.toString())

        ref.setValue(gameState.toDto())
    }

    fun updateDiceSelection(gameUuid: UUID, playerIndex: Int, index: Int, value: Boolean) {
        val ref = database.getReference(
            gameUuid.toString().plus("/players/${playerIndex}/diceSet/$index/selected")
        )

        ref.setValue(value)
    }
}

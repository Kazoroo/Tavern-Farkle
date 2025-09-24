package pl.kazoroo.tavernFarkle.multiplayer.data.remote

import com.google.firebase.Firebase
import com.google.firebase.database.database
import pl.kazoroo.tavernFarkle.core.domain.model.GameState

class FirebaseDataSource {
    val database = Firebase.database

    fun setGameState(gameState: GameState) {
        val myRef = database.getReference(gameState.gameUuid.toString())

        myRef.setValue(gameState.toDto())
    }
}

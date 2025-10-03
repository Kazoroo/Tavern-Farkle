package pl.kazoroo.tavernFarkle.multiplayer.data.remote

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.tasks.await
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import pl.kazoroo.tavernFarkle.multiplayer.data.model.GameStateDto
import pl.kazoroo.tavernFarkle.multiplayer.data.model.Lobby
import pl.kazoroo.tavernFarkle.multiplayer.data.model.PlayerDto

class FirebaseDataSource {
    val database = Firebase.database

    fun observeGameData(
        gameUuid: String,
        onUpdate: (GameStateDto?) -> Unit
    ) {
        val playersRef = database.getReference(gameUuid)

        playersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newGameState = snapshot.getValue<GameStateDto>()
                onUpdate(newGameState)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error occurred during fetching lobby list: $error")
                FirebaseCrashlytics.getInstance().recordException(error.toException())
            }
        })
    }

    fun setGameState(gameState: GameState) {
        val ref = database.getReference(gameState.gameUuid.toString())

        ref.setValue(gameState.toDto())
    }

    fun updateDiceSelection(gameUuid: String, playerIndex: Int, index: Int, value: Boolean) {
        val ref = database.getReference(
            gameUuid.plus("/players/${playerIndex}/diceSet/$index/selected")
        )

        ref.setValue(value)
    }

    fun observeLobbyList(onUpdated: (List<Lobby>) -> Unit) {
        val ref = database.getReference("")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lobbies = snapshot.children.mapNotNull {
                    it.getValue(Lobby::class.java)
                }
                onUpdated(lobbies)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error occurred during fetching lobby list: $error")
                FirebaseCrashlytics.getInstance().recordException(error.toException())
            }
        })
    }

    fun addPlayerToLobby(gameUuid: String, playerData: PlayerDto) {
        val ref = database.getReference("$gameUuid/players/1")

        ref.setValue(playerData)
    }

    suspend fun readGameData(gameUuid: String): GameStateDto? {
        val ref = database.getReference(gameUuid)
        val snapshot = ref.get().await()
        return snapshot.getValue(GameStateDto::class.java)
    }

    fun updateSelectedPoints(gameUuid: String, playerIndex: Int, value: Int) {
        val ref = database.getReference("$gameUuid/players/$playerIndex/selectedPoints")

        ref.setValue(value)
    }

    fun updatePlayers(gameUuid: String, playerIndex: Int, value: PlayerDto) {
        val ref = database.getReference("$gameUuid/players/$playerIndex")

        ref.setValue(value)
    }

    fun updateCurrentPlayerUuid(gameUuid: String, value: String) {
        val ref = database.getReference("$gameUuid/currentPlayerUuid")

        ref.setValue(value)
    }
}
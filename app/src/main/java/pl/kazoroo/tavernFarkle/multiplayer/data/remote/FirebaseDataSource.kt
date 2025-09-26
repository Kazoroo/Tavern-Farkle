package pl.kazoroo.tavernFarkle.multiplayer.data.remote

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import pl.kazoroo.tavernFarkle.core.domain.model.GameState
import pl.kazoroo.tavernFarkle.multiplayer.data.model.Lobby
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
}

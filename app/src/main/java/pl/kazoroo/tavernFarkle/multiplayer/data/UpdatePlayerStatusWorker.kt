package pl.kazoroo.tavernFarkle.multiplayer.data

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import pl.kazoroo.tavernFarkle.multiplayer.data.remote.FirebaseDataSource
import pl.kazoroo.tavernFarkle.multiplayer.data.remote.PlayerStatus

class UpdatePlayerStatusWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val gameUuid = inputData.getString("gameUuid") ?: return Result.failure()
        val playerIndex = inputData.getInt("playerIndex", -1)
        val status = inputData.getString("status") ?: return Result.failure()
        val timestamp = inputData.getLong("timestamp", 0L)

        if (playerIndex == -1 || timestamp == 0L) return Result.failure()
        val database = FirebaseDataSource()

        try {
            database.updatePlayerStatus(gameUuid, playerIndex, PlayerStatus.valueOf(status))
            database.updatePlayerTimestamp(gameUuid, playerIndex, timestamp)
        } catch (e: Exception) {
            return Result.retry()
        }

        return Result.success()
    }
}
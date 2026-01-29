package pl.kazoroo.tavernFarkle.core.domain.usecase.userdata

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey
import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository

@Suppress("UNCHECKED_CAST")
class ReadUserDataUseCase(private val userDataRepository: UserDataRepository) {
    operator fun <T> invoke(key: UserDataKey): T = runBlocking(Dispatchers.IO) {
        when (key) {
            UserDataKey.COINS -> userDataRepository.getStringFlow(key).first() ?: "200"
            UserDataKey.IS_SOUND_ENABLED,
            UserDataKey.IS_MUSIC_ENABLED,
            UserDataKey.IS_FIRST_LAUNCH,
            UserDataKey.IS_FIRST_GAME -> userDataRepository.getBooleanFlow(key).first() ?: true
        } as T
    }
}

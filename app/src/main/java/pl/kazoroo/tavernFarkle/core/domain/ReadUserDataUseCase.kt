package pl.kazoroo.tavernFarkle.core.domain

import kotlinx.coroutines.flow.first
import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey
import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository
import kotlin.reflect.cast

@Suppress("UNCHECKED_CAST")
class ReadUserDataUseCase(private val userDataRepository: UserDataRepository) {
    suspend operator fun <T : Any> invoke(key: UserDataKey): T {
        val flow = when (key.type) {
            String::class -> userDataRepository.getStringFlow(key)
            Boolean::class -> userDataRepository.getBooleanFlow(key)
            else -> throw IllegalArgumentException("Unsupported type: ${key.type}")
        }

        val value: Any = flow.first() ?: getDefaultValue(key)

        return key.type.cast(value) as T
    }

    private fun <T : Any> getDefaultValue(key: UserDataKey): T {
        return when (key) {
            UserDataKey.COINS -> "200" as T
            UserDataKey.IS_SOUND_ENABLED -> true as T
        }
    }
}

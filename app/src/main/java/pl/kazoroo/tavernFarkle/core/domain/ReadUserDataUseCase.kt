package pl.kazoroo.tavernFarkle.core.domain

import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey
import pl.kazoroo.tavernFarkle.core.data.local.UserDataRepository

class ReadUserDataUseCase(private val userDataRepository: UserDataRepository) {
    suspend operator fun invoke(key: String = UserDataKey.COINS.name): String {
        val value = userDataRepository.readValue(key)

        return if(key == UserDataKey.COINS.name && value == null) {
            "200"
        } else value ?: "0"
    }
}
package pl.kazoroo.tavernFarkle.core.domain

import kotlinx.coroutines.flow.first
import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository

class ReadUserDataUseCase(private val userDataRepository: UserDataRepository) {
    suspend operator fun invoke(): String {
        val coins = userDataRepository.userCoins.first()

        return coins.ifEmpty { "200" }
    }
}
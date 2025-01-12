package pl.kazoroo.tavernFarkle.core.domain

import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey
import pl.kazoroo.tavernFarkle.core.data.local.UserDataRepository

class SaveUserDataUseCase(private val userDataRepository: UserDataRepository) {
    suspend operator fun invoke(
        key: UserDataKey = UserDataKey.COINS,
        value: String
    ) {
        userDataRepository.saveNewValue(
            key = key,
            value = value
        )
    }
}
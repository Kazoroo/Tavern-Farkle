package pl.kazoroo.tavernFarkle.core.domain.usecase.userdata

import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey
import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository

class SaveUserDataUseCase(private val userDataRepository: UserDataRepository) {
    suspend operator fun <T : Any> invoke(value: T, key: UserDataKey) {
        require(value == key) {
            "Invalid type for key ${key.name}. Expected: ${key}, Actual: ${value::class.simpleName}"
        }
        userDataRepository.saveValue(value, key)
    }
}
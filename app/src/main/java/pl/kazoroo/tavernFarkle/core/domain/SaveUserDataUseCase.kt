package pl.kazoroo.tavernFarkle.core.domain

import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey
import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository

class SaveUserDataUseCase(private val userDataRepository: UserDataRepository) {
    suspend operator fun <T : Any> invoke(value: T, key: UserDataKey) {
        require(value::class == key.type) {
            "Invalid type for key ${key.name}. Expected: ${key.type.simpleName}, Actual: ${value::class.simpleName}"
        }
        userDataRepository.saveValue(value, key)
    }
}
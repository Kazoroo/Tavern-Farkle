package pl.kazoroo.tavernFarkle.core.domain

import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository

class SaveUserDataUseCase(private val userDataRepository: UserDataRepository) {
    suspend operator fun invoke(
        value: String
    ) {
        userDataRepository.saveNewValue(
            value = value
        )
    }
}
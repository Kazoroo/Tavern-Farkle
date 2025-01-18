package pl.kazoroo.tavernFarkle.shop.domain.usecase

import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice
import pl.kazoroo.tavernFarkle.shop.data.repository.InventoryDataRepository
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDice

sealed class Result {
    data object NotEnoughCoins : Result()
    data class Success(val purchasedDice: SpecialDice) : Result()
}

class BuySpecialDiceUseCase(private val inventoryDataRepository: InventoryDataRepository) {
    suspend operator fun invoke(
        specialDice: SpecialDice,
        coinsAmount: Int,
        takeCoins: () -> Unit
    ): Result {
        val allDice = inventoryDataRepository.getAllSpecialDice()
        val existingDice = allDice.find { it.name == specialDice.name }

        if(coinsAmount >= specialDice.price) {
            takeCoins()
            addOrUpdateNewSpecialDice(existingDice, specialDice)

            return Result.Success(specialDice)
        } else {
            return Result.NotEnoughCoins
        }
    }

    private suspend fun addOrUpdateNewSpecialDice(
        existingDice: OwnedSpecialDice?,
        specialDice: SpecialDice
    ) {
        if (existingDice != null) {
            val updatedDice = existingDice.copy(
                count = existingDice.count + 1,
                isSelected = existingDice.isSelected + false
            )
            inventoryDataRepository.addNewSpecialDice(updatedDice)
        } else {
            val newDice = OwnedSpecialDice(
                name = specialDice.name,
                count = 1,
                isSelected = listOf(false)
            )
            inventoryDataRepository.addNewSpecialDice(newDice)
        }
    }
}
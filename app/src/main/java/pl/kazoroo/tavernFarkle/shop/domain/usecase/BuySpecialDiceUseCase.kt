package pl.kazoroo.tavernFarkle.shop.domain.usecase

import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice
import pl.kazoroo.tavernFarkle.shop.data.repository.InventoryDataRepository
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

class BuySpecialDiceUseCase(private val inventoryDataRepository: InventoryDataRepository) {
    suspend operator fun invoke(name: SpecialDiceName) {
        val allDice = inventoryDataRepository.getAllSpecialDice()
        val existingDice = allDice.find { it.name == name }

        if (existingDice != null) {
            val updatedDice = existingDice.copy(
                count = existingDice.count + 1,
                isSelected = existingDice.isSelected + false
            )
            inventoryDataRepository.addNewSpecialDice(updatedDice)
        } else {
            val newDice = OwnedSpecialDice(
                name = name,
                count = 1,
                isSelected = listOf(false)
            )
            inventoryDataRepository.addNewSpecialDice(newDice)
        }
    }
}
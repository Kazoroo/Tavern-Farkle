package pl.kazoroo.tavernFarkle.shop.data.repository

import kotlinx.coroutines.flow.Flow
import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

interface InventoryDataRepository {
    suspend fun addNewSpecialDice(dice: OwnedSpecialDice)
    suspend fun updateExistingSpecialDice(dice: OwnedSpecialDice)
    suspend fun updateSelectedStatus(name: SpecialDiceName, index: Int)
    suspend fun getAllSpecialDice(): Flow<List<OwnedSpecialDice>>
}
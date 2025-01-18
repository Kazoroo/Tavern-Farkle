package pl.kazoroo.tavernFarkle.shop.data.repository

import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice
import pl.kazoroo.tavernFarkle.shop.data.model.SpecialDiceName

interface InventoryDataRepository {
    suspend fun addNewSpecialDice(dice: OwnedSpecialDice)
    suspend fun updateSelectedStatus(name: SpecialDiceName, index: Int)
    suspend fun getAllSpecialDice(): List<OwnedSpecialDice>
}
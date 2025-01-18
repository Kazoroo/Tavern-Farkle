package pl.kazoroo.tavernFarkle.shop.domain

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first
import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice
import pl.kazoroo.tavernFarkle.shop.data.model.SpecialDiceName
import pl.kazoroo.tavernFarkle.shop.data.repository.InventoryDataRepository

class InventoryDataRepositoryImpl(private val protoDataStore: DataStore<List<OwnedSpecialDice>>) :
    InventoryDataRepository {
    override suspend fun addNewSpecialDice(dice: OwnedSpecialDice) {
        protoDataStore.updateData { list ->
            list + dice
        }
    }

    override suspend fun updateSelectedStatus(name: SpecialDiceName, index: Int) {
        protoDataStore.updateData { list ->
            list.map { item ->
                if (item.name == name) {
                    item.copy(
                        isSelected = item.isSelected.mapIndexed { i, value ->
                            if(i == index) !value else value
                        }
                    )
                } else item
            }
        }
    }

    override suspend fun getAllSpecialDice(): List<OwnedSpecialDice> {
        return protoDataStore.data.first()
    }
}

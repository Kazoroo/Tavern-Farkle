package pl.kazoroo.tavernFarkle.shop.domain

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice
import pl.kazoroo.tavernFarkle.shop.data.repository.InventoryDataRepository
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

class InventoryDataRepositoryImpl(private val protoDataStore: DataStore<List<OwnedSpecialDice>>) : InventoryDataRepository {
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

    override suspend fun getAllSpecialDice(): Flow<List<OwnedSpecialDice>> {
        return protoDataStore.data
            .catch { exception ->
                emit(listOf(OwnedSpecialDice()))
                exception.printStackTrace()
            }
    }
}

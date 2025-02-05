package pl.kazoroo.tavernFarkle.shop.domain

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import pl.kazoroo.tavernFarkle.shop.data.OwnedSpecialDiceSerializer
import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice
import pl.kazoroo.tavernFarkle.shop.data.repository.InventoryDataRepository
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

private val Context.protoDataStore by dataStore(
    fileName = "dice_inventory.json",
    serializer = OwnedSpecialDiceSerializer
)

class InventoryDataRepositoryImpl private constructor(
    private val protoDataStore: DataStore<List<OwnedSpecialDice>>
) : InventoryDataRepository {
    companion object {
        @Volatile
        private var instance: InventoryDataRepositoryImpl? = null

        fun getInstance(context: Context): InventoryDataRepositoryImpl {
            return instance ?: synchronized(this) {
                instance ?: InventoryDataRepositoryImpl(context.applicationContext.protoDataStore)
                    .also { instance = it }
            }
        }
    }

    override suspend fun addNewSpecialDice(dice: OwnedSpecialDice) {
        protoDataStore.updateData { list ->
            list.toMutableList().apply { add(dice) }
        }
    }

    override suspend fun updateExistingSpecialDice(dice: OwnedSpecialDice) {
        protoDataStore.updateData { list ->
            list.map { item ->
                if (item.name == dice.name) dice else item
            }
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
                emit(emptyList())
                exception.printStackTrace()
            }
    }
}

package pl.kazoroo.tavernFarkle.shop.presentation.inventory

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice

class InventoryViewModel(
    private val dataStore: DataStore<List<OwnedSpecialDice>>
): ViewModel() {
    private val _ownedSpecialDice = MutableStateFlow(listOf(OwnedSpecialDice()))
    val ownedSpecialDice: StateFlow<List<OwnedSpecialDice>> get() = _ownedSpecialDice

    //TODO: use repository or usecase instead of directly fetching data from data layer
    init {
        viewModelScope.launch {
            dataStore.data
                .catch { exception ->
                    _ownedSpecialDice.value = listOf(OwnedSpecialDice(count = 77))
                    exception.printStackTrace()
                }
                .collect { data ->
                    _ownedSpecialDice.value = data
                }
        }
    }
}
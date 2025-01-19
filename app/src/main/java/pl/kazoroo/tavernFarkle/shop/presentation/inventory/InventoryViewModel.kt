package pl.kazoroo.tavernFarkle.shop.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice
import pl.kazoroo.tavernFarkle.shop.data.repository.InventoryDataRepository

class InventoryViewModel(
    private val inventoryDataRepository: InventoryDataRepository
): ViewModel() {
    private val _ownedSpecialDice = MutableStateFlow(listOf(OwnedSpecialDice()))
    val ownedSpecialDice: StateFlow<List<OwnedSpecialDice>> get() = _ownedSpecialDice

    //TODO: use repository or usecase instead of directly fetching data from data layer
    init {
        viewModelScope.launch {
            try {
                inventoryDataRepository.getAllSpecialDice()
                    .collect { data ->
                        _ownedSpecialDice.value = data
                    }
            } catch (exception: Exception) {
                _ownedSpecialDice.value = listOf(OwnedSpecialDice())
                exception.printStackTrace()
            }
        }
    }
}
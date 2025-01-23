package pl.kazoroo.tavernFarkle.shop.presentation.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice
import pl.kazoroo.tavernFarkle.shop.data.repository.InventoryDataRepository
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

class InventoryViewModel(
    private val inventoryDataRepository: InventoryDataRepository
): ViewModel() {
    private val _ownedSpecialDice = MutableStateFlow(listOf(OwnedSpecialDice()))
    val ownedSpecialDice: StateFlow<List<OwnedSpecialDice>> get() = _ownedSpecialDice

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

    fun updateSelectedStatus(name: SpecialDiceName, index: Int) {
        viewModelScope.launch {
            inventoryDataRepository.updateSelectedStatus(name, index)
        }

        _ownedSpecialDice.update { state ->
            state.mapIndexed { i, item ->
                if(item.name == name) {
                    item.copy(
                        isSelected = item.isSelected.map {
                            if(i == index) !it else it
                        }
                    )
                } else {
                    item
                }
            }
        }

        println(_ownedSpecialDice.value.joinToString())
    }
}
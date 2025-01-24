package pl.kazoroo.tavernFarkle.shop.presentation.inventory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.shop.data.model.OwnedSpecialDice
import pl.kazoroo.tavernFarkle.shop.data.repository.InventoryDataRepository
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName

class InventoryViewModel(
    private val inventoryDataRepository: InventoryDataRepository
): ViewModel() {
    private val _ownedSpecialDice = MutableStateFlow(listOf(OwnedSpecialDice()))
    val ownedSpecialDice: StateFlow<List<OwnedSpecialDice>> = _ownedSpecialDice

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage

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

    fun updateSelectedStatus(name: SpecialDiceName, index: Int, context: Context) {
        val selectedDiceCount = _ownedSpecialDice.value.sumOf { dice -> dice.isSelected.count { it } }
        val targetDice = _ownedSpecialDice.value.find { it.name == name }!!

        if(selectedDiceCount < 6 || targetDice.isSelected[index]) {
            viewModelScope.launch {
                inventoryDataRepository.updateSelectedStatus(name, index)
            }

            targetDice.let {
                val updatedDice = it.copy(
                    isSelected = it.isSelected.toMutableList().also { list -> list[index] = !list[index] }
                )
                _ownedSpecialDice.update { state ->
                    state.toMutableList().apply {
                        set(indexOf(targetDice), updatedDice)
                    }
                }
            }
        } else {
            viewModelScope.launch {
                _toastMessage.emit(context.getString(R.string.max_number_of_dice_selected))
            }
        }
    }
}
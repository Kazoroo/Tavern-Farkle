package pl.kazoroo.tavernFarkle.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey
import pl.kazoroo.tavernFarkle.core.domain.ReadUserDataUseCase
import pl.kazoroo.tavernFarkle.core.domain.SaveUserDataUseCase
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDice

class ShopViewModel(
    private val saveUserDataUseCase: SaveUserDataUseCase,
    private val readUserDataUseCase: ReadUserDataUseCase,
    private val takeCoins: (Int) -> Unit
): ViewModel() {
    fun buySpecialDice(specialDice: SpecialDice) {
        viewModelScope.launch {
            takeCoins(specialDice.price)
            saveUserDataUseCase.invoke(
                key = UserDataKey.ODD_DICE,
                value = (readUserDataUseCase.invoke(key = UserDataKey.ODD_DICE).toInt() + 1).toString()
            )
        }
    }
}
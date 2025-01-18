package pl.kazoroo.tavernFarkle.shop.presentation.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDice
import pl.kazoroo.tavernFarkle.shop.domain.usecase.BuySpecialDiceUseCase

class ShopViewModel(
    private val buySpecialDiceUseCase: BuySpecialDiceUseCase,
    private val takeCoins: (Int) -> Unit
): ViewModel() {
    fun buySpecialDice(specialDice: SpecialDice) {
        viewModelScope.launch {
            takeCoins(specialDice.price)
            buySpecialDiceUseCase(specialDice.name)
        }
    }
}
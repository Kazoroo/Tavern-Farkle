package pl.kazoroo.tavernFarkle.shop.presentation

import androidx.lifecycle.ViewModel
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDice

class ShopViewModel(
    private val takeCoins: (Int) -> Unit
): ViewModel() {
    fun buySpecialDice(specialDice: SpecialDice) {
        takeCoins(specialDice.price)
        //TODO: Adding buyed dice to the datastore
    }
}
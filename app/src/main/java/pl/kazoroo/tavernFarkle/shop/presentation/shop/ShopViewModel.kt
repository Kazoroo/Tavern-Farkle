package pl.kazoroo.tavernFarkle.shop.presentation.shop

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDice
import pl.kazoroo.tavernFarkle.shop.domain.usecase.BuySpecialDiceUseCase
import pl.kazoroo.tavernFarkle.shop.domain.usecase.Result

class ShopViewModel(
    private val buySpecialDiceUseCase: BuySpecialDiceUseCase,
    private val coinsAmount: Int,
    private val takeCoins: (Int) -> Unit
): ViewModel() {
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage

    fun buySpecialDice(specialDice: SpecialDice, context: Context) {
        viewModelScope.launch {
            val result = buySpecialDiceUseCase(
                specialDice = specialDice,
                coinsAmount = coinsAmount,
                takeCoins = { takeCoins(specialDice.price) }
            )

            when(result) {
                is Result.NotEnoughCoins -> {
                    _toastMessage.emit(context.getString(R.string.not_enough_coins))
                }
                is Result.Success -> {
                    _toastMessage.emit(
                        context.getString(
                            R.string.successfully_purchased,
                            specialDice.name.displayName
                        )
                    )
                }
            }
        }
    }
}
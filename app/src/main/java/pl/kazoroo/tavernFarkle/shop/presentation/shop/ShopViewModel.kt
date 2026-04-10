package pl.kazoroo.tavernFarkle.shop.presentation.shop

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey
import pl.kazoroo.tavernFarkle.core.domain.usecase.userdata.ReadUserDataUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.userdata.SaveUserDataUseCase
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDice
import pl.kazoroo.tavernFarkle.shop.domain.usecase.BuySpecialDiceUseCase
import pl.kazoroo.tavernFarkle.shop.domain.usecase.Result
import pl.kazoroo.tavernFarkle.shop.presentation.components.ShopRevealableKeys

class ShopViewModel(
    private val buySpecialDiceUseCase: BuySpecialDiceUseCase,
    readUserDataUseCase: ReadUserDataUseCase,
    private val saveUserDataUseCase: SaveUserDataUseCase
): ViewModel() {
    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage

    private val _onboardingStage = MutableStateFlow(ShopRevealableKeys.SpecialDice.ordinal)
    val onboardingStage: StateFlow<Int> = _onboardingStage.asStateFlow()

    private val _isFirstShopOpen = MutableStateFlow<Boolean>(readUserDataUseCase(UserDataKey.IS_FIRST_SHOP_OPEN))
    val isFirstShopOpen: StateFlow<Boolean> = _isFirstShopOpen.asStateFlow()

    fun nextOnboardingStage() {
        _onboardingStage.value++
    }

    fun finishOnboarding() {
        _isFirstShopOpen.value = false

        viewModelScope.launch {
            saveUserDataUseCase(false, UserDataKey.IS_FIRST_SHOP_OPEN)
        }
    }

    fun buySpecialDice(
        specialDice: SpecialDice,
        context: Context,
        coinsAmount: Int,
        readCoins: () -> Unit,
        takeCoins: () -> Unit
    ) {
        viewModelScope.launch {
            val result = buySpecialDiceUseCase(
                specialDice = specialDice,
                coinsAmount = coinsAmount,
                takeCoins = takeCoins
            )

            when(result) {
                is Result.NotEnoughCoins -> {
                    _toastMessage.emit(context.getString(R.string.not_enough_coins))
                }
                is Result.Success -> {
                    _toastMessage.emit(
                        context.getString(
                            R.string.successfully_purchased,
                            context.getString(specialDice.name.displayNameRes)
                        )
                    )

                    readCoins()
                }
            }
        }
    }
}

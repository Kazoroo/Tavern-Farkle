package pl.kazoroo.tavernFarkle.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey
import pl.kazoroo.tavernFarkle.core.domain.usecase.userdata.ReadUserDataUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.userdata.SaveUserDataUseCase

class CoinsViewModel(
    private val saveUserDataUseCase: SaveUserDataUseCase,
    private val readUserDataUseCase: ReadUserDataUseCase
) : ViewModel() {
    private val _betValue = MutableStateFlow("0")

    private val _coinsAmount = MutableStateFlow("0")
    val coinsAmount = _coinsAmount.asStateFlow()

    init {
        viewModelScope.launch {
            _coinsAmount.value = readCoinsAmount()
        }
    }

    fun grantRewardCoins(rewardAmount: String) {
        viewModelScope.launch {
            val coins = readCoinsAmount()
            val newCoinBalance = (coins.toInt() + rewardAmount.toInt()).toString()

            saveUserDataUseCase.invoke(
                value = newCoinBalance,
                key = UserDataKey.COINS
            )
            readCoinsAmount()
            _coinsAmount.value = newCoinBalance
        }
    }

    fun setBetValue(value: String) {
        _betValue.value = value

        viewModelScope.launch {
            val coins = readCoinsAmount()
            val newCoinBalance = (coins.toInt() - value.toInt()).toString()
            saveUserDataUseCase.invoke(
                value = newCoinBalance,
                key = UserDataKey.COINS
            )
            readCoinsAmount()
        }
    }

    private fun readCoinsAmount(): String {
        val coins = readUserDataUseCase.invoke<String>(UserDataKey.COINS)

        _coinsAmount.value = coins

        return coins
    }

    fun addBetCoinsToTotalCoinsAmount() {
        viewModelScope.launch {
            _coinsAmount.value = (coinsAmount.value.toInt() + _betValue.value.toInt() * 2).toString()

            saveUserDataUseCase.invoke(
                value = _coinsAmount.value,
                key = UserDataKey.COINS
            )
            readCoinsAmount()
        }
    }

    fun takeCoinsFromWallet(amount: Int) {
        viewModelScope.launch {
            val newCoinBalance = (coinsAmount.value.toInt() - amount).toString()

            saveUserDataUseCase.invoke(
                value = newCoinBalance,
                key = UserDataKey.COINS
            )

            readCoinsAmount()
        }
    }
}
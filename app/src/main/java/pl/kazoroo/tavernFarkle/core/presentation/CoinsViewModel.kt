package pl.kazoroo.tavernFarkle.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey
import pl.kazoroo.tavernFarkle.core.domain.usecase.userdata.ReadUserDataUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.userdata.SaveUserDataUseCase
import pl.kazoroo.tavernFarkle.menu.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.menu.sound.SoundType
import kotlin.math.abs

class CoinsViewModel(
    private val saveUserDataUseCase: SaveUserDataUseCase,
    private val readUserDataUseCase: ReadUserDataUseCase
) : ViewModel() {
    private val _betValue = MutableStateFlow("0")
    val betValue = _betValue.asStateFlow()

    private val _coinsAmount = MutableStateFlow("0")
    val coinsAmount = _coinsAmount.asStateFlow()

    private val _coinsAmountAfterBetting = MutableStateFlow(0)
    val coinsAmountAfterBetting = _coinsAmountAfterBetting.asStateFlow()

    init {
        viewModelScope.launch {
            _coinsAmount.value = readCoinsAmount()
        }
    }

    fun handleGameEndRewards(isWin: Boolean) {
        viewModelScope.launch {
            delay(1000L)

            if (isWin) {
                if(coinsAmountAfterBetting.value == 0 && betValue.value.toInt() == 0) {
                    takeCoinsFromWallet(coinsAmount.value.toInt() - 50)
                    SoundPlayer.playSound(SoundType.FALLING_COINS)
                } else {
                    addBetCoinsToTotalCoinsAmount()
                }
            } else {
                if(coinsAmountAfterBetting.value == 0) {
                    takeCoinsFromWallet(coinsAmount.value.toInt() - 50)
                    SoundPlayer.playSound(SoundType.FALLING_COINS)
                } else {
                    takeCoinsFromWallet(betValue.value.toInt())
                }
            }
        }
    }

    fun grantRewardCoins(rewardAmount: String) {
        viewModelScope.launch {
            val coins = readCoinsAmount()
            var newCoinBalance = (coins.toInt() + rewardAmount.toInt())
            if(newCoinBalance < 0) newCoinBalance = Int.MAX_VALUE
            val newCoinBalanceString = newCoinBalance.toString()

            saveUserDataUseCase.invoke(
                value = newCoinBalanceString,
                key = UserDataKey.COINS
            )
            readCoinsAmount()
            _coinsAmount.value = newCoinBalanceString
        }
    }

    fun setBetValue(betAmount: String) {
        _betValue.value = betAmount

        _coinsAmountAfterBetting.value = coinsAmount.value.toInt() - betAmount.toInt()
    }

    private fun readCoinsAmount(): String {
        val coins = readUserDataUseCase.invoke<String>(UserDataKey.COINS)
        val absoluteCoinsValue = abs(coins.toInt()).toString()

        _coinsAmount.value = absoluteCoinsValue

        return absoluteCoinsValue
    }

    fun addBetCoinsToTotalCoinsAmount() {
        viewModelScope.launch {
            var coins = (coinsAmount.value.toInt() + _betValue.value.toInt())
            if(coins < 0) coins = Int.MAX_VALUE
            _coinsAmount.value = coins.toString()

            saveUserDataUseCase.invoke(
                value = _coinsAmount.value,
                key = UserDataKey.COINS
            )
            readCoinsAmount()

            if(betValue.value.toInt() != 0) {
                SoundPlayer.playSound(SoundType.FALLING_COINS)
            }
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
package pl.kazoroo.tavernFarkle.shop.presentation.shop

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.windedge.table.components.Divider
import kotlinx.coroutines.flow.collectLatest
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.data.local.repository.SpecialDiceList.specialDiceList
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.core.presentation.components.BackgroundImage
import pl.kazoroo.tavernFarkle.core.presentation.components.CoinAmountIndicator
import pl.kazoroo.tavernFarkle.game.presentation.components.ButtonInfo
import pl.kazoroo.tavernFarkle.game.presentation.components.DiceButton
import pl.kazoroo.tavernFarkle.shop.domain.usecase.BuySpecialDiceUseCase
import pl.kazoroo.tavernFarkle.shop.presentation.AdViewModel
import pl.kazoroo.tavernFarkle.shop.presentation.components.SpecialDiceCard

@Composable
fun ShopScreen(
    coinsViewModel: CoinsViewModel,
    adViewModel: AdViewModel = viewModel(),
    buySpecialDiceUseCase: BuySpecialDiceUseCase
) {
    val viewModel =  remember {
        ShopViewModel(
            buySpecialDiceUseCase,
            coinsViewModel.coinsAmount.value.toInt()
        ) { amount ->
            coinsViewModel.takeCoinsFromWallet(amount)
        }
    }
    val context = LocalContext.current
    val coinsAmount = coinsViewModel.coinsAmount.collectAsState().value

    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            CoinAmountIndicator(
                coinsAmount = coinsAmount,
                modifier = Modifier.align(Alignment.Start)
            )

            LazyColumn {
                item {
                    RewardedVideoSection(adViewModel, context, coinsViewModel)
                }
                
                items(specialDiceList.size) { index ->
                    SpecialDiceCard(
                        name = specialDiceList[index].name,
                        image = specialDiceList[index].image[0],
                        chancesOfDrawingValue = specialDiceList[index].chancesOfDrawingValue,
                        price = specialDiceList[index].price,
                        onClick = {
                            viewModel.buySpecialDice(
                                specialDiceList[index],
                                context = context
                            )
                        },
                        coinsAmount = coinsAmount.toInt()
                    )
                }
            }
        }
    }
}

@Composable
private fun RewardedVideoSection(
    adViewModel: AdViewModel,
    context: Context,
    coinsViewModel: CoinsViewModel
) {
    DiceButton(
        buttonInfo = ButtonInfo(
            text = stringResource(R.string.get_100_coins),
            onClick = {
                adViewModel.showRewardedAd(
                    context = context,
                    onReward = {
                        coinsViewModel.grantRewardCoins(it)
                    }
                )
            }
        ),
        modifier = Modifier
            .height(dimensionResource(R.dimen.game_button_height))
            .padding(dimensionResource(id = R.dimen.medium_padding))
    )

    Divider(
        thickness = 1.dp,
        modifier = Modifier
            .background(
                brush = Brush.horizontalGradient(
                    colorStops = arrayOf(
                        0.0f to Color.Transparent,
                        0.4f to Color.White,
                        0.8f to Color.Gray,
                        1.0f to Color.Transparent
                    )
                )
            )
            .height(1.dp),
        color = Color.Transparent
    )
}

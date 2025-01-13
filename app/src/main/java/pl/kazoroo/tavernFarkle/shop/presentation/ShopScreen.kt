package pl.kazoroo.tavernFarkle.shop.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.windedge.table.components.Divider
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.domain.ReadUserDataUseCase
import pl.kazoroo.tavernFarkle.core.domain.SaveUserDataUseCase
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.core.presentation.components.CoinAmountIndicator
import pl.kazoroo.tavernFarkle.game.presentation.components.ButtonInfo
import pl.kazoroo.tavernFarkle.game.presentation.components.DiceButton
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDice
import pl.kazoroo.tavernFarkle.shop.presentation.components.SpecialDiceCard

@Composable
fun ShopScreen(
    coinsViewModel: CoinsViewModel,
    adViewModel: AdViewModel = viewModel(),
    saveUserDataUseCase: SaveUserDataUseCase,
    readUserDataUseCase: ReadUserDataUseCase
) {
    val viewModel =  remember {
        ShopViewModel(
            saveUserDataUseCase = saveUserDataUseCase,
            readUserDataUseCase = readUserDataUseCase,
        ) { amount ->
            coinsViewModel.takeCoinsFromWallet(amount)
        }
    }

    val context = LocalContext.current
    val specialDiceList = listOf(
        SpecialDice(
            name = "Odd dice",
            price = 2,
            image = R.drawable.odd_dice_1,
            chancesOfDrawingValue = listOf(26.7f, 6.7f, 26.7f, 6.7f, 26.7f, 6.7f),
        ),
        SpecialDice(
            name = "Alfonse's dice",
            price = 1000,
            image = R.drawable.alfonses_dice_1,
            chancesOfDrawingValue = listOf(38.5f, 7.7f, 7.7f, 7.7f, 15.4f, 23f),
        ),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.wooden_background_texture),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CoinAmountIndicator(
                coinsAmount = coinsViewModel.coinsAmount.collectAsState().value,
                modifier = Modifier.align(Alignment.Start)
            )

            LazyColumn {
                item {
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
                            .padding(
                                start = dimensionResource(id = R.dimen.small_padding),
                                bottom = dimensionResource(id = R.dimen.medium_padding),
                                end = dimensionResource(id = R.dimen.small_padding),
                                top = dimensionResource(id = R.dimen.medium_padding)
                            )
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
                
                items(specialDiceList.size) { index ->
                    SpecialDiceCard(
                        specialDice = specialDiceList[index],
                        onClick = {
                            viewModel.buySpecialDice(specialDiceList[index])
                        }
                    )
                }
            }
        }
    }
}

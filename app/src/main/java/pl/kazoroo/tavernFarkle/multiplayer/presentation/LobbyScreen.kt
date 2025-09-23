package pl.kazoroo.tavernFarkle.multiplayer.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.core.presentation.components.BackgroundImage
import pl.kazoroo.tavernFarkle.core.presentation.components.CoinAmountIndicator
import pl.kazoroo.tavernFarkle.core.presentation.navigation.Screen
import pl.kazoroo.tavernFarkle.game.presentation.mainmenu.components.BettingDialog
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundType
import pl.kazoroo.tavernFarkle.shop.presentation.inventory.InventoryViewModel

data class Lobby(
    val lobbyId: String,
    val bet: Int
)

val lobbyList = listOf(
    Lobby("1", 100),
    Lobby("2", 200),
    Lobby("3", 300),
    Lobby("4", 400),
)

@Composable
fun LobbyScreen(
    coinsAmount: String,
    navController: NavHostController,
    lobbyViewModel: LobbyViewModel,
    inventoryViewModel: InventoryViewModel,
    coinsViewModel: CoinsViewModel
) {
    var isBettingDialogVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()

        Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
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
                    items(lobbyList.size) { index ->
                        LobbyCard(lobbyList[index])
                    }
                }
            }

            Button(
                onClick = {
                    isBettingDialogVisible = true
                },
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(R.dimen.medium_padding),
                        vertical = dimensionResource(R.dimen.medium_padding),
                    )
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.menu_button_height))
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.create_lobby),
                    fontWeight = FontWeight.W800
                )
            }
        }

        if(isBettingDialogVisible) {
            BettingDialog(
                onCloseClick = {
                    isBettingDialogVisible = false
                },
                onClick = { betAmount ->
                    lobbyViewModel.startNewGame(
                        betAmount.toInt(),
                        inventoryViewModel.getSelectedSpecialDiceNames()
                    )
                    SoundPlayer.playSound(SoundType.CLICK)
                    navController.navigate(Screen.GameScreen.withArgs(true))
                    coinsViewModel.setBetValue(betAmount)
                },
                coinsAmount = coinsAmount.toInt()
            )
        }
    }
}

@Composable
private fun LobbyCard(lobbyData: Lobby) {
    Card(
        modifier = Modifier.padding(
            horizontal = dimensionResource(R.dimen.medium_padding),
            vertical = dimensionResource(R.dimen.small_padding),
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bet: ${lobbyData.bet}",
                modifier = Modifier.padding(
                    start = dimensionResource(R.dimen.medium_padding)
                )
            )

            Image(
                painter = painterResource(R.drawable.coin),
                contentDescription = "Coin icon",
                modifier = Modifier
                    .size(dimensionResource(R.dimen.coin_icon_size))
                    .padding(start = dimensionResource(R.dimen.small_padding))
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {},
                modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(stringResource(R.string.join))
            }
        }
    }
}
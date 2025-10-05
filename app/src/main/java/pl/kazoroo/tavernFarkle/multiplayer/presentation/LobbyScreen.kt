package pl.kazoroo.tavernFarkle.multiplayer.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.core.presentation.components.BackgroundImage
import pl.kazoroo.tavernFarkle.core.presentation.components.BettingDialog
import pl.kazoroo.tavernFarkle.core.presentation.components.CoinAmountIndicator
import pl.kazoroo.tavernFarkle.core.presentation.navigation.Screen
import pl.kazoroo.tavernFarkle.multiplayer.data.model.Lobby
import pl.kazoroo.tavernFarkle.shop.presentation.inventory.InventoryViewModel

@Composable
fun LobbyScreen(
    coinsAmount: String,
    navController: NavHostController,
    lobbyViewModel: LobbyViewModel,
    inventoryViewModel: InventoryViewModel,
    coinsViewModel: CoinsViewModel
) {
    var isBettingDialogVisible by remember { mutableStateOf(false) }
    val lobbyList = lobbyViewModel.lobbyList.collectAsState().value

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()

        Box(modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                CoinAmountIndicator(
                    coinsAmount = coinsAmount,
                    modifier = Modifier.align(Alignment.Start)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    LobbyList(lobbyList, lobbyViewModel, inventoryViewModel, navController, coinsViewModel)
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
                        .height(dimensionResource(R.dimen.menu_button_height)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.create_lobby),
                        fontWeight = FontWeight.W800
                    )
                }
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
                        inventoryViewModel.getSelectedSpecialDiceNames(),
                        onNavigate = { navController.navigate(Screen.GameScreen.withArgs(true)) },
                        setBetValue = { bet ->
                            coinsViewModel.setBetValue(bet)
                        }
                    )
                },
                coinsAmount = coinsAmount.toInt()
            )
        }
    }
}

@Composable
private fun BoxScope.LobbyList(
    lobbyList: List<Lobby>,
    lobbyViewModel: LobbyViewModel,
    inventoryViewModel: InventoryViewModel,
    navController: NavHostController,
    coinsViewModel: CoinsViewModel
) {
    if (lobbyList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_lobbies_available_you_can_wait_or_create_one),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            items(lobbyList.size) { index ->
                LobbyCard(
                    lobbyData = lobbyList[index],
                    onJoinClick = {
                        lobbyViewModel.joinLobby(
                            gameUuid = lobbyList[index].gameUuid,
                            selectedSpecialDiceNames = inventoryViewModel.getSelectedSpecialDiceNames(),
                            onNavigate = { navController.navigate(Screen.GameScreen.withArgs(true)) },
                            bet = lobbyList[index].betAmount,
                            setBetValue = { bet ->
                                coinsViewModel.setBetValue(bet)
                            }
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun LobbyCard(lobbyData: Lobby, onJoinClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(
            horizontal = dimensionResource(R.dimen.medium_padding),
            vertical = dimensionResource(R.dimen.small_padding),
        ).fillMaxWidth().height(120.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bet: ${lobbyData.betAmount}",
                )

                Image(
                    painter = painterResource(R.drawable.coin),
                    contentDescription = "Coin icon",
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.coin_icon_size))
                        .padding(start = dimensionResource(R.dimen.small_padding))
                )
            }

            Text(
                text = "${lobbyData.playerCount} / 2\nplayers",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .border(1.dp, Color.LightGray, RoundedCornerShape(5.dp))
                    .padding(dimensionResource(R.dimen.small_padding))
            )

            Button(
                onClick = onJoinClick,
                shape = RoundedCornerShape(12.dp),
                enabled = lobbyData.playerCount == 1,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.join),
                    fontWeight = FontWeight.W700,
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    }
}
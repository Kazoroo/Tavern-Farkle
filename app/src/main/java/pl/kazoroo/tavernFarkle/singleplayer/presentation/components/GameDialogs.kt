package pl.kazoroo.tavernFarkle.singleplayer.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.menu.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.menu.sound.SoundType
import pl.kazoroo.tavernFarkle.singleplayer.presentation.GameViewModel
import pl.kazoroo.tavernFarkle.ui.theme.DarkRed

@Composable
fun GameDialogs(
    isSkuchaDialogVisible: Boolean,
    coinsViewModel: CoinsViewModel,
    isGameResultDialogVisible: Boolean,
    isOpponentTurn: Boolean,
    viewModel: GameViewModel,
    continueToMenu: () -> Unit
) {
    val coinsBefore = coinsViewModel.coinsAmountAfterBetting.collectAsState()
    val betValue = coinsViewModel.betValue.collectAsState()

    if (isSkuchaDialogVisible) {
        GameResultAndSkuchaDialog(
            text = "Skucha!",
            textColor = Color(212, 212, 212),
            extraText = null,
            displayButton = false
        )
    }

    if (isGameResultDialogVisible && isOpponentTurn) {
        SoundPlayer.playSound(SoundType.FAILURE)

        GameResultAndSkuchaDialog(
            text = stringResource(R.string.you_lost),
            textColor = DarkRed,
            extraText = if (coinsBefore.value == 0) stringResource(R.string._50_starter_coins_to_continue_playing)
                else stringResource(R.string.minus_coins, betValue.value),
            onClick = continueToMenu
        )
    } else if (isGameResultDialogVisible) {
        SoundPlayer.playSound(SoundType.WIN)

        GameResultAndSkuchaDialog(
            text = stringResource(R.string.you_win),
            textColor = Color.Green,
            extraText = if (coinsBefore.value == 0 && betValue.value.toInt() == 0) stringResource(R.string._50_starter_coins_to_continue_playing)
                else stringResource(R.string.plus_coins, betValue.value),
            onClick = continueToMenu
        )
    }

    if (viewModel.playerQuit) {
        SoundPlayer.playSound(SoundType.WIN)

        GameResultAndSkuchaDialog(
            text = "Win by giving up",
            textColor = Color.Green,
            extraText = if (coinsBefore.value == 0 && betValue.value.toInt() == 0) stringResource(R.string._50_starter_coins_to_continue_playing)
                else stringResource(R.string.opponent_leave_the_game_coins, betValue.value),
            onClick = continueToMenu
        )
    }

    if (viewModel.timerValue > -1) {
        GameResultAndSkuchaDialog(
            text = viewModel.timerValue.toString(),
            textColor = Color.White,
            extraText = stringResource(R.string.waiting_for_opponent_to_return),
            displayButton = false
        )
    }
}

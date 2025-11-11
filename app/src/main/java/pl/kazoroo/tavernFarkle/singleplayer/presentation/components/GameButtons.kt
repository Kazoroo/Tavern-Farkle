package pl.kazoroo.tavernFarkle.singleplayer.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import pl.kazoroo.tavernFarkle.R

@Composable
fun GameButtons(buttonsInfo: List<ButtonInfo>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 15.dp)
    ) {
        buttonsInfo.forEach { buttonInfo ->
            DiceButton(
                buttonInfo,
                modifier = Modifier
                    .height(dimensionResource(R.dimen.game_button_height))
                    .weight(0.5f)
                    .padding(
                        start = dimensionResource(id = R.dimen.small_padding),
                        end = dimensionResource(id = R.dimen.small_padding),
                        bottom = dimensionResource(R.dimen.buttons_vertical_padding)
                    )
            )
        }
    }
}
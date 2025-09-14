package pl.kazoroo.tavernFarkle.game.presentation.game.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.game.presentation.components.ButtonInfo
import pl.kazoroo.tavernFarkle.game.presentation.components.DiceButton

@Composable
fun GameButtons(buttonsInfo: List<ButtonInfo>) {
    Row(
        modifier = Modifier.fillMaxWidth()
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
                    ),
                backgroundContentScale = ContentScale.FillBounds
            )
        }
    }
}
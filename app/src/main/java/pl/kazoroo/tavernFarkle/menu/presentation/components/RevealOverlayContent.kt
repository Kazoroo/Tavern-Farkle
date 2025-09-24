package pl.kazoroo.tavernFarkle.menu.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.svenjacobs.reveal.Key
import com.svenjacobs.reveal.RevealOverlayArrangement
import com.svenjacobs.reveal.RevealOverlayScope
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.menu.presentation.RevealableKeys

@Composable
fun RevealOverlayScope.RevealOverlayContent(key: Key) {
    when (key) {
        RevealableKeys.SpeedDialMenu -> {
            OverlayItem(
                alignModifier = Modifier.align(horizontalArrangement = RevealOverlayArrangement.Start),
                text = stringResource(R.string.here_you_can_read_the_rules)
            )
        }

        RevealableKeys.ShopButton -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top),
                text = stringResource(R.string.in_shop_you_buy_special_dice_that_gives_you_advantage)
            )
        }

        RevealableKeys.InventoryButton -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Bottom),
                text = stringResource(R.string.in_inventory_you_select_dice_you_want_to_use_in_game)
            )
        }
    }
}

@Composable
private fun RevealOverlayScope.OverlayItem(alignModifier: Modifier, text: String) {
    Surface(
        modifier = alignModifier
            .padding(8.dp),
        shape = RoundedCornerShape(4.dp),
        color = Color.White,
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(dimensionResource(R.dimen.small_padding))
        )
    }
}
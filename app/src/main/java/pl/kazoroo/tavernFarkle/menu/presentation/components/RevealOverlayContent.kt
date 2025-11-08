package pl.kazoroo.tavernFarkle.menu.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
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
        RevealableKeys.Welcome -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top),
                text = "Welcome to Tavern Farkle!\n Roll the dice, score as many points as you can, and stay ahead of your opponent."
            )
        }

        RevealableKeys.Skucha -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Bottom),
                text = "But be careful! If you roll and score nothing, you get skucha and lose your points for the round."
            )
        }

        RevealableKeys.SinglePlayer -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top),
                text = "Play solo against the computer. Tap here to start a game and place your bet."
            )
        }

        RevealableKeys.MultiPlayer -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top),
                text = "Multiplayer lets you play with real players, such as your friends. Join a lobby or create your own."
            )
        }

        RevealableKeys.HowToPlay -> {
            OverlayItem(
                alignModifier = Modifier.align(horizontalArrangement = RevealOverlayArrangement.Start),
                text = "Not sure how to play Farkle? Click here to learn the rules. You can also check them anytime while playing."
            )
        }

        RevealableKeys.ShopButton -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top),
                text = "A good strategy starts with choosing the right dice. Tap here to open the shop and buy special dice."
            )
        }

        RevealableKeys.InventoryButton -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top),
                text = "After buying special dice, don’t forget to select them in your Inventory. You can choose up to 6 special dice."
            )
        }
    }
}

@Composable
private fun OverlayItem(alignModifier: Modifier, text: String) {
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
package pl.kazoroo.tavernFarkle.shop.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.svenjacobs.reveal.Key
import com.svenjacobs.reveal.RevealOverlayArrangement
import com.svenjacobs.reveal.RevealOverlayScope
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.presentation.components.OverlayItem

@Composable
fun RevealOverlayScope.ShopRevealOverlayContent(key: Key) {
    when (key) {
        ShopRevealableKeys.SpecialDice -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top),
                text = stringResource(R.string.onboarding_special_dice)
            )
        }
        ShopRevealableKeys.Strategy -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top),
                text = stringResource(R.string.onboarding_strategy)
            )
        }
    }
}

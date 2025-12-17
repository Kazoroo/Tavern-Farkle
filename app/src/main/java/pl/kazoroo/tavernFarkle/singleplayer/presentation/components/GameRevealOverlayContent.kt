package pl.kazoroo.tavernFarkle.singleplayer.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.svenjacobs.reveal.Key
import com.svenjacobs.reveal.RevealOverlayArrangement
import com.svenjacobs.reveal.RevealOverlayScope
import pl.kazoroo.tavernFarkle.core.presentation.components.OverlayItem
import pl.kazoroo.tavernFarkle.singleplayer.presentation.GameRevealableKeys

@Composable
fun RevealOverlayScope.GameRevealOverlayContent(key: Key) {
    when (key) {
        GameRevealableKeys.ScoringDice -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top),
                text = "Click dice with value 1 or 5 to get points."
            )
        }
        GameRevealableKeys.ScoreButton -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top, horizontalAlignment = Alignment.Start),
                text = "Tap here to score your points and play further."
            )
        }
        GameRevealableKeys.ThreeOfKindFirstDice -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top, horizontalAlignment = Alignment.End, confineWidth = false),
                text = "Three or more of kind also gives you points. Select first one."
            )
        }
        GameRevealableKeys.ThreeOfKindSecondDice -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Bottom, horizontalAlignment = Alignment.Start),
                text = "Select second dice of kind..."
            )
        }
        GameRevealableKeys.ThreeOfKindThirdDice -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Bottom, horizontalAlignment = Alignment.End),
                text = "...and third."
            )
        }
        GameRevealableKeys.PassButton -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top, horizontalAlignment = Alignment.End),
                text = "If you don't want to risk more, click Pass to finish your round. Then your opponent takes their turn."
            )
        }
    }
}
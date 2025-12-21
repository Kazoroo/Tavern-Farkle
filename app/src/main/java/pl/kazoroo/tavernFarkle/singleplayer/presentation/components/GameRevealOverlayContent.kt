package pl.kazoroo.tavernFarkle.singleplayer.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.svenjacobs.reveal.Key
import com.svenjacobs.reveal.RevealOverlayArrangement
import com.svenjacobs.reveal.RevealOverlayScope
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.presentation.components.OverlayItem
import pl.kazoroo.tavernFarkle.singleplayer.presentation.GameRevealableKeys

@Composable
fun RevealOverlayScope.GameRevealOverlayContent(key: Key) {
    when (key) {
        GameRevealableKeys.ScoringDice -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top),
                text = stringResource(R.string.click_dice_with_value_1_or_5_to_get_points)
            )
        }
        GameRevealableKeys.ScoreButton -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top, horizontalAlignment = Alignment.Start),
                text = stringResource(R.string.tap_here_to_score_your_points_and_play_further)
            )
        }
        GameRevealableKeys.ThreeOfKindFirstDice -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top, horizontalAlignment = Alignment.End, confineWidth = false),
                text = stringResource(R.string.three_or_more_of_kind_also_gives_you_points_select_first_one)
            )
        }
        GameRevealableKeys.ThreeOfKindSecondDice -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Bottom, horizontalAlignment = Alignment.Start, confineWidth = false),
                text = stringResource(R.string.select_second_dice_of_kind)
            )
        }
        GameRevealableKeys.ThreeOfKindThirdDice -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Bottom, horizontalAlignment = Alignment.End),
                text = stringResource(R.string.and_third)
            )
        }
        GameRevealableKeys.SelectedPoints -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Bottom),
                text = stringResource(R.string.as_you_see_three_of_a_kind_or_more_scores_the_die_value_100_points)
            )
        }
        GameRevealableKeys.PassButton -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top, horizontalAlignment = Alignment.End),
                text = stringResource(R.string.if_you_don_t_want_to_risk_more_click_pass_to_finish_your_round_then_your_opponent_takes_their_turn)
            )
        }
    }
}
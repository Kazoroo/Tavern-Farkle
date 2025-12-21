package pl.kazoroo.tavernFarkle.menu.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.svenjacobs.reveal.Key
import com.svenjacobs.reveal.RevealOverlayArrangement
import com.svenjacobs.reveal.RevealOverlayScope
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.presentation.components.OverlayItem
import pl.kazoroo.tavernFarkle.menu.presentation.RevealableKeys

@Composable
fun RevealOverlayScope.RevealOverlayContent(key: Key) {
    when (key) {
        RevealableKeys.Welcome -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top),
                text = stringResource(R.string.welcome_to_tavern_farkle_roll_the_dice_score_as_many_points_as_you_can_and_stay_ahead_of_your_opponent)
            )
        }

        RevealableKeys.Skucha -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Bottom),
                text = stringResource(R.string.but_be_careful_if_you_roll_and_score_nothing_you_get_skucha_and_lose_your_points_for_the_round)
            )
        }

        RevealableKeys.SinglePlayer -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top),
                text = stringResource(R.string.play_solo_against_the_computer_tap_here_to_start_a_game_and_place_your_bet)
            )
        }

        RevealableKeys.MultiPlayer -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top),
                text = stringResource(R.string.multiplayer_lets_you_play_with_real_players_such_as_your_friends_join_a_lobby_or_create_your_own)
            )
        }

        RevealableKeys.HowToPlay -> {
            OverlayItem(
                alignModifier = Modifier.align(horizontalArrangement = RevealOverlayArrangement.Start),
                text = stringResource(R.string.not_sure_how_to_play_farkle_click_here_to_learn_the_rules_you_can_also_check_them_anytime_while_playing)
            )
        }

        RevealableKeys.ShopButton -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top),
                text = stringResource(R.string.a_good_strategy_starts_with_choosing_the_right_dice_tap_here_to_open_the_shop_and_buy_special_dice)
            )
        }

        RevealableKeys.InventoryButton -> {
            OverlayItem(
                alignModifier = Modifier.align(verticalArrangement = RevealOverlayArrangement.Top),
                text = stringResource(R.string.after_buying_special_dice_don_t_forget_to_select_them_in_your_inventory_you_can_choose_up_to_6_special_dice)
            )
        }
    }
}
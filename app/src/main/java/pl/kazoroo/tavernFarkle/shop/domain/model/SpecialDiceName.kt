package pl.kazoroo.tavernFarkle.shop.domain.model

import androidx.annotation.StringRes
import pl.kazoroo.tavernFarkle.R

enum class SpecialDiceName(@StringRes val displayNameRes: Int) {
    ODD_DICE(R.string.odd_dice),
    ALFONSES_DICE(R.string.alfonse_s_dice),
    SPIDERS_DICE(R.string.spider_s_dice),
}
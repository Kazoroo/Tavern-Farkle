package pl.kazoroo.tavernFarkle.core.presentation.components

import androidx.compose.animation.core.EaseOutSine
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pl.kazoroo.tavernFarkle.R

@Composable
fun CoinAmountIndicator(
    coinsAmount: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(
                start = dimensionResource(R.dimen.small_padding)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val coins = animateIntAsState(
            coinsAmount.toInt(),
            animationSpec = tween(
                easing = EaseOutSine,
                durationMillis = 800
            )
        )
        val coinsText = coins.value
            .toString()
            .reversed()
            .chunked(3)
            .joinToString(" ")
            .reversed()

        Text(
            text = coinsText,
            color = Color.White
        )

        Spacer(modifier = Modifier.width(4.dp))

        Image(
            painter = painterResource(R.drawable.coin),
            contentDescription = "Coin icon",
            modifier = Modifier
                .size(dimensionResource(R.dimen.coin_icon_size))
                .padding(3.dp)
        )
    }
}
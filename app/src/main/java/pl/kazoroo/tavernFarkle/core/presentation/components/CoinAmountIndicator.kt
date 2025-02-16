package pl.kazoroo.tavernFarkle.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
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
        Text(
            text = coinsAmount,
            color = Color.White
        )

        Image(
            painter = painterResource(R.drawable.coin),
            contentDescription = "Coin icon",
            modifier = Modifier
                .size(dimensionResource(R.dimen.coin_icon_size))
                .padding(start = dimensionResource(R.dimen.small_padding))
        )
    }
}
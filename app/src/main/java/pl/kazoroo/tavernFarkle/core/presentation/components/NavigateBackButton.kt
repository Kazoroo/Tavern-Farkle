package pl.kazoroo.tavernFarkle.core.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import pl.kazoroo.tavernFarkle.R

@Composable
fun NavigateBackButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(R.drawable.arrow_back_24dp_white),
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier.size(dimensionResource(R.dimen.coin_icon_size))
        )
    }
}
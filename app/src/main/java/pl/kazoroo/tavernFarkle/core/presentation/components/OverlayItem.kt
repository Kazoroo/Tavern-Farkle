package pl.kazoroo.tavernFarkle.core.presentation.components

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
import pl.kazoroo.tavernFarkle.R

@Composable
fun OverlayItem(alignModifier: Modifier, text: String) {
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
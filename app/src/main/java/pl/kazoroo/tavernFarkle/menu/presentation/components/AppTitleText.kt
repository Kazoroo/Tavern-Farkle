package pl.kazoroo.tavernFarkle.menu.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import pl.kazoroo.tavernFarkle.R

@Composable
fun AppTitleText() {
    Box {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge.copy(
                drawStyle = Stroke(
                    width = 14f,
                    join = StrokeJoin.Round,
                    miter = 10f
                )
            ),
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge.copy(
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(-20f, 15f),
                    blurRadius = 10f
                )
            ),
            color = Color.Yellow,
            textAlign = TextAlign.Center
        )
    }
}
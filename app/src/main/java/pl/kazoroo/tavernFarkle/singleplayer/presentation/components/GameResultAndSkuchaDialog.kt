package pl.kazoroo.tavernFarkle.singleplayer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import pl.kazoroo.tavernFarkle.R

@Composable
fun GameResultAndSkuchaDialog(text: String, extraText: String?, textColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.60f)
                .background(
                    color = Color(26, 26, 26, 200),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner))
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.small_padding))
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge.copy(
                        drawStyle = Stroke(
                            width = 14f,
                            join = StrokeJoin.Round,
                            miter = 10f
                        )
                    ),
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge.copy(
                        shadow = Shadow(
                            color = Color.Black,
                            offset = Offset(-20f, 15f),
                            blurRadius = 20f
                        )
                    ),
                    color = textColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                )
            }

            if (extraText != null) {
                Text(
                    text = extraText,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.displayMedium,
                    modifier = Modifier
                        .padding(vertical = dimensionResource(id = R.dimen.medium_padding), horizontal = dimensionResource(R.dimen.small_padding))
                )
            }
        }
    }
}
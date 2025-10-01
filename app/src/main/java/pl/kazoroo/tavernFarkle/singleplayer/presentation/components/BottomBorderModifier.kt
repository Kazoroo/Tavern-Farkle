package pl.kazoroo.tavernFarkle.singleplayer.presentation.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

/**
 * Adds a line below the element, that is slightly longer than the element itself.
 *
 * @param strokeWidth width of the line given in Dp
 * @param color color of the line
 */
fun Modifier.bottomBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val height = size.height - strokeWidthPx / 2

            drawLine(
                brush = Brush.horizontalGradient(
                    colorStops = arrayOf(
                        0.0f to Color.Transparent,
                        0.3f to color,
                        1.0f to Color.Transparent
                    ),
                    startX = -20f,
                    endX = size.width + 20f
                ),
                start = Offset(x = -20f, y = height),
                end = Offset(x = size.width + 20f, y = height),
                strokeWidth = strokeWidthPx,
                cap = StrokeCap.Round
            )
        }
    }
)
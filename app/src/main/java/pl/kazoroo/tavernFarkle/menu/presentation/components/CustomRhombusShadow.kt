package pl.kazoroo.tavernFarkle.menu.presentation.components

import android.graphics.BlurMaskFilter
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp

@Composable
fun CustomRhombusShadow(shadowSize: Dp) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //Shadow works only on API 29+
        Box(
            modifier = Modifier
                .size(shadowSize)
                .drawBehind {
                    rotate(45f) {
                        val blurRadius = 40f

                        val paint = Paint().asFrameworkPaint().apply {
                            color = Color(0x40000000).toArgb()
                            maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
                        }

                        drawIntoCanvas {
                            it.nativeCanvas.drawRect(
                                200f,
                                200f,
                                size.width - 30,
                                size.height - 30,
                                paint
                            )
                        }
                    }
                }
        )
    }
}
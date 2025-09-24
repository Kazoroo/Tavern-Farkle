package pl.kazoroo.tavernFarkle.singleplayer.presentation.components

import android.graphics.BlurMaskFilter
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.domain.model.Dice

@Composable
fun InteractiveDiceLayout(
    diceState: List<Dice>,
    diceOnClick: (Int) -> Unit,
    isDiceClickable: Boolean,
    isDiceAnimating: Boolean
) {
    Column(
        modifier = Modifier
            .padding(vertical = 30.dp, horizontal = dimensionResource(id = R.dimen.small_padding))
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp
        val imageSize = (screenWidth.dp / 3) - 10.dp

        for (row in 0..1) {
            AnimatedVisibility(
                visible = !isDiceAnimating
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (column in 0..2) {
                        val index = row * 3 + column

                        AnimatedVisibility(
                            visible = diceState[index].isVisible,
                            enter = slideInHorizontally(
                                initialOffsetX = {
                                    (if(index == 0 || index == 3) -it else it) * 3
                                }
                            ),
                            exit = slideOutHorizontally(
                                targetOffsetX = {
                                    (if(index == 0 || index == 3) -it else it) * 3
                                }
                            )
                        ) {
                            DiceImageWithShadow(imageSize, diceState[index], index, isDiceClickable, diceOnClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiceImageWithShadow(
    imageSize: Dp,
    diceState: Dice,
    index: Int,
    isDiceClickable: Boolean,
    diceOnClick: (Int) -> Unit
) {
    CustomDiceShadow(shadowSize = imageSize)

    Image(
        painter = painterResource(id = diceState.image),
        contentDescription = "Dice $index",
        modifier = Modifier
            .padding(2.dp)
            .size(imageSize)
            .animatedCircularBorder(
                isSelected = diceState.isSelected
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                enabled = isDiceClickable
            ) {
                diceOnClick(index)
            }
    )
}

@Composable
private fun CustomDiceShadow(shadowSize: Dp) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //Shadow works only on API 29+
        Box(
            modifier = Modifier
                .size(shadowSize)
                .drawBehind {
                    val blurRadius = 25f

                    val paint = Paint().asFrameworkPaint().apply {
                        color = Color(0x32000000).toArgb()
                        maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
                    }

                    drawIntoCanvas {
                        it.nativeCanvas.drawOval(
                            50f,
                            160f,
                            size.width - 40,
                            size.height + 40f,
                            paint
                        )
                    }
                }
        )
    }
}

@Composable
fun Modifier.animatedCircularBorder(
    isSelected: Boolean,
    color: Color = Color.Red,
    borderWidth: Dp = 3.dp
): Modifier {
    val sweepAngle by animateFloatAsState(
        targetValue = if (isSelected) 360f else 0f,
        animationSpec = tween(durationMillis = 300, easing = EaseInOutCubic),
        label = "Animation of drawing circle around dice"
    )

    return this.drawBehind {
        if (sweepAngle > 0) {
            val strokeWidth = borderWidth.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = -sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(
                    x = (size.width - 2 * radius) / 2,
                    y = (size.height - 2 * radius) / 2
                ),
                size = Size(radius * 2, radius * 2)
            )
        }
    }
}
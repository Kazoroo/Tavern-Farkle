package pl.kazoroo.tavernFarkle.game.presentation.game.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.game.domain.model.DiceSetInfo

@Composable
fun InteractiveDiceLayout(
    diceState: DiceSetInfo,
    diceOnClick: (Int) -> Unit,
    isDiceClickable: Boolean,
    isDiceAnimating: Boolean,
    isDiceVisibleAfterGameEnd: List<Boolean>
) {
    Column(
        modifier = Modifier
            .padding(vertical = 30.dp, horizontal = dimensionResource(id = R.dimen.small_padding))
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp
        val imageSize = (screenWidth / 3) - 10.dp
        val localDensity = LocalDensity.current

        for (row in 0..1) {
            AnimatedVisibility(
                visible = !isDiceAnimating
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (column in 0..2) {
                        val index = row * 3 + column
                        val offsetX by animateDpAsState(
                            targetValue = if (!isDiceVisibleAfterGameEnd[index]) 0.dp else imageSize * 3,
                            label = ""
                        )
                        val offsetLambda: () -> IntOffset = {
                            with(localDensity) {
                                IntOffset(offsetX.toPx().toInt(), 0)
                            }
                        }

                        AnimatedVisibility(
                            visible = diceState.isDiceVisible[index],
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
                            Image(
                                painter = painterResource(id = diceState.diceList[index].image),
                                contentDescription = "Dice $index",
                                modifier = Modifier
                                    .padding(2.dp)
                                    .size(imageSize)
                                    .animatedCircularBorder(
                                        isSelected = diceState.isDiceSelected[index]
                                    )
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() },
                                        enabled = isDiceClickable
                                    ) {
                                        diceOnClick(index)
                                    }
                                    .offset { offsetLambda() }
                            )
                        }
                    }
                }
            }
        }
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
        label = ""
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
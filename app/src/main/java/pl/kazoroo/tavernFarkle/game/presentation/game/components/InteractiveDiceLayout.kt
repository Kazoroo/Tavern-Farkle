package pl.kazoroo.tavernFarkle.game.presentation.game.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
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
                                    .border(
                                        if (diceState.isDiceSelected[index]) 2.dp else (-1).dp,
                                        Color.Red,
                                        RoundedCornerShape(100)
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
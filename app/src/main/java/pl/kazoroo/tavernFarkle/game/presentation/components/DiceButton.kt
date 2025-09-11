package pl.kazoroo.tavernFarkle.game.presentation.components

import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import pl.kazoroo.tavernFarkle.R

@Composable
fun DiceButton(
    buttonInfo: ButtonInfo,
    modifier: Modifier = Modifier
) {
    val alphaBackground by animateFloatAsState(
        targetValue = if (buttonInfo.enabled) 1f else 0.4f,
        animationSpec = tween(durationMillis = 100, easing = EaseInOutQuad),
        label = "Animate game buttons opacity"
    )

    Button(
        onClick = buttonInfo.onClick,
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)),
        modifier = modifier
            .border(
                width = 1.dp,
                color = Color.DarkGray,
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner))
            ).dropShadow(
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)),
                shadow = Shadow(
                    color = Color(0x60000000),
                    radius = 10.dp,
                    offset = DpOffset(10.dp, 15.dp)
                ),
            ),
        enabled = buttonInfo.enabled,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.glossy_button),
                contentDescription = stringResource(R.string.score_and_roll_again),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner))),
                alpha = alphaBackground
            )

            Text(
                text = buttonInfo.text,
                textAlign = TextAlign.Center,
                color = Color.Black,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}
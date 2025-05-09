package pl.kazoroo.tavernFarkle.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pl.kazoroo.tavernFarkle.R

@Composable
fun BackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.wooden_background_texture),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize().blur(4.dp)
    )
}
package pl.kazoroo.tavernFarkle.core.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import pl.kazoroo.tavernFarkle.R

@Composable
fun BackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.background_wooden),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}
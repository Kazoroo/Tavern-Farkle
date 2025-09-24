package pl.kazoroo.tavernFarkle.menu.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import pl.kazoroo.tavernFarkle.R

@Composable
fun DialogHeader(
    headerText: String,
) {
    Text(
        text = headerText,
        style = MaterialTheme.typography.titleSmall,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = dimensionResource(id = R.dimen.medium_padding),
                bottom = dimensionResource(id = R.dimen.medium_padding)
            )
    )
}
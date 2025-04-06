package pl.kazoroo.tavernFarkle.game.presentation.mainmenu.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import pl.kazoroo.tavernFarkle.R

@Composable
fun HowToPlayDialog(
    onCloseClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onCloseClick
    ) {
        LazyColumn(
            modifier = Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(dimensionResource(R.dimen.medium_padding))
                )
        ) {
            item {
                DialogHeader(
                    headerText = stringResource(R.string.how_to_play)
                )
            }

            item {
                Text(
                    text = stringResource(R.string.how_to_play_long_tutorial),
                    modifier = Modifier
                        .padding(dimensionResource(R.dimen.medium_padding))
                )
            }
        }
    }
}

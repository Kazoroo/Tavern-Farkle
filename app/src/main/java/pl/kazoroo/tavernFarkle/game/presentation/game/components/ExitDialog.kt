package pl.kazoroo.tavernFarkle.game.presentation.game.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import pl.kazoroo.tavernFarkle.R

@Composable
fun ExitDialog(
    onDismissClick: () -> Unit,
    onQuitClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissClick,
        confirmButton = {
            TextButton(
                onClick = onQuitClick,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.medium_padding)
                )
            ) {
                Text(text = stringResource(R.string.yes))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissClick,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.medium_padding)
                ),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(text = stringResource(R.string.no))
            }
        },
        text = {
            Text(
                text = stringResource(R.string.do_you_really_want_to_leave_a_match),
                style = MaterialTheme.typography.labelLarge
            )
        },
        title = {
            Text(
                text = stringResource(R.string.leaving_a_match),
                style = MaterialTheme.typography.titleSmall
            )
        }
    )
}
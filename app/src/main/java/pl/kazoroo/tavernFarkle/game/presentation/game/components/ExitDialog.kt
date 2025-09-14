package pl.kazoroo.tavernFarkle.game.presentation.game.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
            Button(
                onClick = onQuitClick,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.small_padding)
                )
            ) {
                Text(text = stringResource(R.string.leave))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissClick,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(R.dimen.small_padding)
                )
            ) {
                Text(text = stringResource(R.string.cancel))
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
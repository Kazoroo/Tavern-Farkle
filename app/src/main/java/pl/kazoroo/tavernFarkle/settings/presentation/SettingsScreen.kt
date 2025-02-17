package pl.kazoroo.tavernFarkle.settings.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.kazoroo.tavernFarkle.R

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val isSoundEnabled = remember { mutableStateOf(true) }
    val isMusicEnabled = remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.wooden_background_texture),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .systemBarsPadding()
                .padding(start = dimensionResource(R.dimen.small_padding))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.sound),
                    modifier = Modifier.width(dimensionResource(R.dimen.dialog_text_field_width)),
                    color = Color.White
                )

                Switch(
                    checked = isSoundEnabled.value,
                    onCheckedChange = { checked ->
                        isSoundEnabled.value = checked
                        viewModel.setSoundEnabled(isSoundEnabled.value)
                    }
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.music),
                    modifier = Modifier.width(dimensionResource(R.dimen.dialog_text_field_width)),
                    color = Color.White
                )

                Switch(
                    checked = isMusicEnabled.value,
                    onCheckedChange = { checked ->
                        isMusicEnabled.value = checked
                    }
                )
            }
        }
        val annotatedText = buildAnnotatedString {
            val text = stringResource(R.string.this_game_is_open_source)
            val start = text.indexOf(stringResource(R.string.open_source))
            val end = start + stringResource(R.string.open_source).length
            append(text)

            addStyle(
                style = SpanStyle(
                    color = Color.Cyan,
                    textDecoration = TextDecoration.Underline
                ),
                start = start,
                end = end
            )

            addLink(
                url = LinkAnnotation.Url("https://github.com/Kazoroo/Tavern-Farkle"),
                start = start,
                end = end
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .systemBarsPadding()
        ) {
            val versionName = remember {
                context.packageManager.getPackageInfo(context.packageName, 0).versionName
            }

            Text(
                text = "$versionName",
                modifier = Modifier.padding(dimensionResource(R.dimen.small_padding)),
                color = Color.White
            )

            Text(
                text = annotatedText,
                color = Color.White
            )
        }
    }
}

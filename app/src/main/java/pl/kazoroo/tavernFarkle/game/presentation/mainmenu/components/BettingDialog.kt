package pl.kazoroo.tavernFarkle.game.presentation.mainmenu.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.ui.theme.DarkRed

@Composable
fun BettingDialog(
    onClick: () -> Unit,
    onCloseClick: () -> Unit,
    coinsViewModel: CoinsViewModel,
) {
    var betAmount by remember { mutableStateOf("0") }
    val coinsAmount = coinsViewModel.coinsAmount.collectAsState().value.toInt()

    Dialog(
        onDismissRequest = onCloseClick
    ) {
        Column(
            modifier = Modifier
                .height(300.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(dimensionResource(R.dimen.medium_padding))
                )
        ) {
            DialogHeader(
                headerText = stringResource(R.string.determining_the_amount)
            )

            val isBetAmountNumeric = betAmount.contains(regex = Regex("^[0-9]*\$"))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.enter_the_amount),
                    modifier = Modifier
                        .padding(
                            start = dimensionResource(R.dimen.large_padding),
                            top = dimensionResource(R.dimen.small_padding)
                        )
                )

                TextField(
                    value = betAmount,
                    onValueChange = { betAmount = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(R.dimen.large_padding)),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = {
                        Image(
                            painter = painterResource(R.drawable.coin),
                            contentDescription = "Coin icon",
                            modifier = Modifier
                                .size(dimensionResource(R.dimen.coin_icon_size))
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent
                    ),
                    supportingText = {
                        Text(
                            text =
                            if(betAmount.isEmpty())
                                stringResource(R.string.text_field_cannot_be_empty)
                            else if(!isBetAmountNumeric)
                                stringResource(R.string.text_field_must_contain_only_numbers_0_9)
                            else if(betAmount.toInt() > coinsAmount)
                                stringResource(R.string.you_can_t_take_more_than_you_have)
                            else "",
                            color = DarkRed
                        )
                    }
                )
            }
            val isBetAmountValid = betAmount.isNotEmpty() && isBetAmountNumeric && betAmount.toInt() <= coinsAmount

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    onClick()
                    coinsViewModel.setBetValue(betAmount)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(
                        bottom = dimensionResource(R.dimen.small_padding),
                        top = dimensionResource(R.dimen.medium_padding)
                    ),
                enabled = isBetAmountValid
            ) {
                Text(
                    text = stringResource(R.string.play),
                    modifier = Modifier.padding(
                        vertical = dimensionResource(R.dimen.small_padding),
                        horizontal = dimensionResource(R.dimen.medium_padding)
                    )
                )
            }
        }
    }
}

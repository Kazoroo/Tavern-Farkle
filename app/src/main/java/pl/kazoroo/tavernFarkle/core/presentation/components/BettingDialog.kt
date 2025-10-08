package pl.kazoroo.tavernFarkle.core.presentation.components

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.menu.presentation.components.DialogHeader
import pl.kazoroo.tavernFarkle.ui.theme.DarkRed

@Composable
fun BettingDialog(
    onClick: (betAmount: String) -> Unit,
    onCloseClick: () -> Unit,
    coinsAmount: Int,
) {
    var betAmount by remember { mutableStateOf("0") }

    Dialog(
        onDismissRequest = onCloseClick
    ) {
        Column(
            modifier = Modifier
                .height(350.dp)
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
                            start = dimensionResource(R.dimen.large_padding)
                        )
                )

                TextField(
                    value = betAmount,
                    onValueChange = { newText ->
                        val filtered = newText.filter { it.isDigit() }

                        if (filtered.length <= Int.MAX_VALUE.toString().length) {
                            betAmount = filtered
                        }
                    },
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
                    onClick(betAmount)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(
                        bottom = dimensionResource(R.dimen.medium_padding),
                        top = dimensionResource(R.dimen.medium_padding)
                    )
                    .dropShadow(
                        shape = RoundedCornerShape(dimensionResource(R.dimen.rounded_corner)),
                        shadow = Shadow(
                            color = Color(0x80000000),
                            radius = 10.dp,
                            offset = DpOffset(5.dp, 12.dp)
                        )
                    ),
                enabled = isBetAmountValid,
                elevation = ButtonDefaults.buttonElevation(5.dp)
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
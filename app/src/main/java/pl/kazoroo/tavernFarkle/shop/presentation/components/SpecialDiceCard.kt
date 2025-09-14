package pl.kazoroo.tavernFarkle.shop.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDiceName
import pl.kazoroo.tavernFarkle.ui.theme.DarkGreen

@Composable
fun SpecialDiceCard(
    name: SpecialDiceName,
    image: Int,
    chancesOfDrawingValue: List<Float>,
    price: Int,
    isInventoryCard: Boolean = false,
    isSelected: Boolean = false,
    coinsAmount: Int = 0,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(dimensionResource(R.dimen.medium_padding))
            .fillMaxWidth()
            .height(dimensionResource(R.dimen.special_dice_card_height))
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.paper_texture),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .padding(
                            top = dimensionResource(R.dimen.medium_padding),
                        )
                ) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.special_dice_icon_size))
                    )

                    Column {
                        Text(
                            text = stringResource(name.displayNameRes),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = dimensionResource(R.dimen.small_padding)),
                            textAlign = TextAlign.Center
                        )

                        Row(
                            modifier = Modifier
                                .widthIn(250.dp, 400.dp)
                                .align(Alignment.CenterHorizontally),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(
                                text = "⚀ - ${chancesOfDrawingValue[0]}%\n" +
                                        "⚁ - ${chancesOfDrawingValue[1]}%\n" +
                                        "⚂ - ${chancesOfDrawingValue[2]}%"
                            )

                            Text(
                                text = "⚃ - ${chancesOfDrawingValue[3]}%\n" +
                                        "⚄ - ${chancesOfDrawingValue[4]}%\n" +
                                        "⚅ - ${chancesOfDrawingValue[5]}%"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onClick,
                    modifier = Modifier
                        .padding(
                            start = dimensionResource(R.dimen.medium_padding),
                            top = 0.dp,
                            end = dimensionResource(R.dimen.medium_padding),
                            bottom = dimensionResource(R.dimen.medium_padding)
                        )
                        .fillMaxWidth()
                        .height(50.dp)
                        .dropShadow(
                            shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)),
                            shadow = Shadow(
                                color = Color(0x50000000),
                                radius = 8.dp,
                                offset = DpOffset(10.dp, 12.dp)
                            ),
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor =
                        if(isSelected || price <= coinsAmount) DarkGreen else Color.Red
                    ),
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner))
                ) {
                    Text(
                        text = if(isInventoryCard) {
                            if(isSelected) stringResource(R.string.selected_text) else stringResource(R.string.select)
                        } else {
                            stringResource(R.string.buy_for, price)
                        },
                        textAlign = TextAlign.Center,
                    )

                    if(!isInventoryCard) {
                        Image(
                            painter = painterResource(R.drawable.coin),
                            contentDescription = "Coin icon",
                            modifier = Modifier
                                .size(dimensionResource(R.dimen.coin_icon_size))
                                .padding(start = dimensionResource(R.dimen.small_padding))
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun x() {
    Box {
        SpecialDiceCard(
            name = SpecialDiceName.SPIDERS_DICE,
            image = R.drawable.spiders_dice_1,
            chancesOfDrawingValue = listOf(12f, 12f, 12f, 12f, 12f, 12f),
            price = 123
        ) { }
    }
}
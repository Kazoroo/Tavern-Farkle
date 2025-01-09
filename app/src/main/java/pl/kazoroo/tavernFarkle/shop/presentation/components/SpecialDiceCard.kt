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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import pl.kazoroo.tavernFarkle.R


@Composable
fun SpecialDiceCard() {
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
                            end = dimensionResource(R.dimen.medium_padding),
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.dice_1),
                        contentDescription = null,
                        modifier = Modifier.size(dimensionResource(R.dimen.special_dice_icon_size))
                    )

                    Column {
                        Text(
                            text = "Ambrose Die",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = dimensionResource(R.dimen.small_padding)),
                            textAlign = TextAlign.Center
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Text(
                                text = "1 - 20%\n2 - 13%\n3 - 36%"
                            )

                            Text(
                                text = "4 - 12%\n5 - 18%\n6 - 41%"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { },
                    modifier = Modifier
                        .padding(
                            horizontal = dimensionResource(R.dimen.medium_padding),
                            vertical = dimensionResource(R.dimen.medium_padding)
                        )
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Buy for 500",
                        textAlign = TextAlign.Center
                    )

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
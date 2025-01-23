package pl.kazoroo.tavernFarkle.shop.presentation.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.data.local.repository.SpecialDiceList.specialDiceList
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDice
import pl.kazoroo.tavernFarkle.shop.presentation.components.SpecialDiceCard

@Composable
fun InventoryScreen(inventoryViewModel: InventoryViewModel) {
    val ownedSpecialDiceList = inventoryViewModel.ownedSpecialDice.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.wooden_background_texture),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if(ownedSpecialDiceList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.your_inventory_is_empty_buy_some_special_dice_in_shop),
                            color = Color.White,
                            modifier = Modifier
                                .width((LocalConfiguration.current.screenWidthDp / 2).dp)
                                .align(Alignment.Center),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                ownedSpecialDiceList.forEach { item ->
                    items(item.count) { index ->
                        val specialDiceData: SpecialDice = specialDiceList.find { item.name == it.name }!!

                        SpecialDiceCard(
                            isInventoryCard = true,
                            name = specialDiceData.name,
                            image = specialDiceData.image,
                            chancesOfDrawingValue = specialDiceData.chancesOfDrawingValue,
                            price = specialDiceData.price,
                            isSelected = item.isSelected[index]
                        ) {
                            inventoryViewModel.updateSelectedStatus(
                                name = specialDiceData.name,
                                index = index
                            )
                        }
                    }
                }
            }
        }
    }
}
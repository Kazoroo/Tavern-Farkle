package pl.kazoroo.tavernFarkle.shop.presentation.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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

        LazyColumn {
            items(ownedSpecialDiceList.size) { index ->
                val ownedDice = ownedSpecialDiceList[index]
                val tempDice: SpecialDice = specialDiceList.find { it.name == ownedDice.name }!!

                SpecialDiceCard(
                    isInventoryCard = true,
                    name = ownedDice.name,
                    image = tempDice.image,
                    chancesOfDrawingValue = tempDice.chancesOfDrawingValue,
                    price = tempDice.price
                ) {
                    //TODO: Select a special dice
                }
            }
        }
    }
}
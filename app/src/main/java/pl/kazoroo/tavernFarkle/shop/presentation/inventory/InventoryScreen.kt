package pl.kazoroo.tavernFarkle.shop.presentation.inventory

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.domain.ReadUserDataUseCase
import pl.kazoroo.tavernFarkle.core.domain.SaveUserDataUseCase
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDice
import pl.kazoroo.tavernFarkle.shop.presentation.components.SpecialDiceCard

@Composable
fun InventoryScreen(
    saveUserDataUseCase: SaveUserDataUseCase,
    readUserDataUseCase: ReadUserDataUseCase
) {
    val viewModel =  remember {
        InventoryViewModel(
            saveUserDataUseCase = saveUserDataUseCase,
            readUserDataUseCase = readUserDataUseCase,
        )
    }

    val tempSpecialDiceList = listOf(
        SpecialDice(
            name = "Odd dice",
            price = 2,
            image = R.drawable.odd_dice_1,
            chancesOfDrawingValue = listOf(26.7f, 6.7f, 26.7f, 6.7f, 26.7f, 6.7f),
        ),
        SpecialDice(
            name = "Odd dice",
            price = 2,
            image = R.drawable.odd_dice_1,
            chancesOfDrawingValue = listOf(26.7f, 6.7f, 26.7f, 6.7f, 26.7f, 6.7f),
        ),
        SpecialDice(
            name = "Alfonse's dice",
            price = 1000,
            image = R.drawable.alfonses_dice_1,
            chancesOfDrawingValue = listOf(38.5f, 7.7f, 7.7f, 7.7f, 15.4f, 23f),
        ),
    )

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
            items(tempSpecialDiceList.size) { index ->
                SpecialDiceCard(
                    tempSpecialDiceList[index],
                    isInventoryCard = true
                ) {
                    //TODO: Select a special dice
                }
            }
        }
    }
}
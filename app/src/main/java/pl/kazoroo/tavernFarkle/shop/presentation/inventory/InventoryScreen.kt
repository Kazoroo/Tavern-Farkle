package pl.kazoroo.tavernFarkle.shop.presentation.inventory

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.flow.collectLatest
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.data.local.repository.SpecialDiceList.specialDiceList
import pl.kazoroo.tavernFarkle.core.presentation.components.BackgroundImage
import pl.kazoroo.tavernFarkle.core.presentation.components.NavigateBackButton
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDice
import pl.kazoroo.tavernFarkle.shop.presentation.components.SpecialDiceCard

@Composable
fun InventoryScreen(
    inventoryViewModel: InventoryViewModel,
    navController: NavController
) {
    val ownedSpecialDiceList = inventoryViewModel.ownedSpecialDice.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(inventoryViewModel.toastMessage) {
        inventoryViewModel.toastMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BackgroundImage()

        Column(
            modifier = Modifier.systemBarsPadding()
        ) {
            NavigateBackButton {
                navController.navigateUp()
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                if (ownedSpecialDiceList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.your_inventory_is_empty_buy_some_special_dice_in_shop),
                                color = Color.White,
                                modifier = Modifier
                                    .width((LocalWindowInfo.current.containerSize / 2).width.dp)
                                    .align(Alignment.Center),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    item {
                        InventoryAdvice()
                    }

                    ownedSpecialDiceList.forEach { item ->
                        items(item.count) { index ->
                            val specialDiceData: SpecialDice = specialDiceList.find { item.name == it.name }!!

                            SpecialDiceCard(
                                isInventoryCard = true,
                                name = specialDiceData.name,
                                image = specialDiceData.image[0],
                                chancesOfDrawingValue = specialDiceData.chancesOfDrawingValue,
                                price = specialDiceData.price,
                                isSelected = item.isSelected[index]
                            ) {
                                inventoryViewModel.updateSelectedStatus(
                                    name = specialDiceData.name,
                                    index = index,
                                    context = context
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun InventoryAdvice() {
    Box(
        modifier = Modifier
            .height(100.dp)
            .padding(horizontal = dimensionResource(R.dimen.medium_padding))
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, Color.DarkGray, RoundedCornerShape(10.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.paper_texture),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                text = stringResource(R.string.select_dice_you_want_to_use_in_the_game),
                modifier = Modifier
                    .padding(dimensionResource(R.dimen.medium_padding)),
                textAlign = TextAlign.Center
            )
        }
    }
}
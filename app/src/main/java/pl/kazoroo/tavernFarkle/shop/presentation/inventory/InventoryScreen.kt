package pl.kazoroo.tavernFarkle.shop.presentation.inventory

import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import pl.kazoroo.tavernFarkle.R
import pl.kazoroo.tavernFarkle.core.data.local.repository.SpecialDiceList.specialDiceList
import pl.kazoroo.tavernFarkle.core.presentation.components.BackgroundImage
import pl.kazoroo.tavernFarkle.core.presentation.components.NavigateBackButton
import pl.kazoroo.tavernFarkle.shop.domain.model.SpecialDice

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
                        InventoryTitleAndAdvice()

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    ownedSpecialDiceList.forEach { item ->
                        items(item.count) { index ->
                            val specialDiceData: SpecialDice = specialDiceList.find { item.name == it.name }!!
                            var bumpTrigger by remember { mutableStateOf(false) }

                            val scale by animateFloatAsState(
                                targetValue = if (bumpTrigger) 1.05f else 1f,
                                animationSpec = tween(durationMillis = 120),
                                label = "scale"
                            )

                            val offsetY by animateFloatAsState(
                                targetValue = if (bumpTrigger) -6f else 0f,
                                animationSpec = tween(durationMillis = 120),
                                label = "offsetY"
                            )

                            LaunchedEffect(item.isSelected[index]) {
                                bumpTrigger = true
                                delay(120)
                                bumpTrigger = false
                            }

                            Card(
                                modifier = Modifier
                                    .padding(horizontal = dimensionResource(R.dimen.medium_padding), vertical = dimensionResource(R.dimen.small_padding))
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .clickable(onClick = {
                                        inventoryViewModel.updateSelectedStatus(
                                            name = specialDiceData.name,
                                            index = index,
                                            context = context
                                        )
                                    })
                                    .dropShadow(
                                        shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)),
                                        shadow = Shadow(
                                            color = Color(0x80000000),
                                            radius = 10.dp,
                                            offset = DpOffset(10.dp, 18.dp)
                                        ),
                                    )
                                    .offset(y = offsetY.dp)
                                    .scale(scale),
                                shape = RoundedCornerShape(dimensionResource(id = R.dimen.rounded_corner)),
                                border = if(item.isSelected[index]) BorderStroke(3.dp, Color.Green) else null
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.parchment_texture),
                                        contentDescription = null,
                                        contentScale = ContentScale.FillWidth,
                                        modifier = Modifier.fillMaxSize()
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Image(
                                            painter = painterResource(id = specialDiceData.image[0]),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxHeight()
                                        )

                                        Text(
                                            text = stringResource(specialDiceData.name.displayNameRes),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = dimensionResource(R.dimen.small_padding)),
                                            textAlign = TextAlign.Center,
                                            fontSize = 25.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun InventoryTitleAndAdvice() {
    Column(
        modifier = Modifier
            .padding(horizontal = dimensionResource(R.dimen.medium_padding)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.inventory),
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.small_padding)),
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp
        )

        Text(
            text = stringResource(R.string.tap_a_die_to_add_or_remove_from_your_set),
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.small_padding)),
            textAlign = TextAlign.Center,
            color = Color(0xFFD2BBBB),
            fontSize = 20.sp,
        )
    }
}
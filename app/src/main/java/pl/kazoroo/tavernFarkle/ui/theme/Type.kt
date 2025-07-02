package pl.kazoroo.tavernFarkle.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import pl.kazoroo.tavernFarkle.R

val imFellEnglish = FontFamily(
    Font(R.font.imfelldwpica_regular, FontWeight.Normal),
    Font(R.font.imfelldwpica_italic, FontWeight.Normal, FontStyle.Italic)
)

val fenwickWoodtype = FontFamily(
    Font(R.font.fenwick_woodtype, FontWeight.Bold)
)

@Composable
fun provideTypography(): Typography {
    val screenWidthDp = LocalConfiguration.current.screenWidthDp

    return Typography(
        bodyLarge = TextStyle(
            fontFamily = imFellEnglish,
            fontWeight = FontWeight.Normal,
            fontSize = if(screenWidthDp < 600) 22.sp else 30.sp,
            color = Color.Black
        ),

        bodySmall = TextStyle(
            fontFamily = imFellEnglish,
            fontWeight = FontWeight.Light,
            fontSize = if(screenWidthDp < 600) 15.sp else 18.sp,
            color = Color.Black
        ),

        labelLarge = TextStyle(
            fontFamily = imFellEnglish,
            fontWeight = FontWeight.Normal,
            fontSize = if(screenWidthDp < 600) 22.sp else 30.sp
        ),

        displayLarge = TextStyle(
            fontFamily = imFellEnglish,
            fontWeight = FontWeight.Bold,
            fontSize = 45.sp,
            shadow = Shadow(color = Color.Black)
        ),

        displayMedium = TextStyle(
            fontFamily = imFellEnglish,
            fontWeight = FontWeight.Normal,
            fontSize = 35.sp,
            shadow = Shadow(color = Color.Black)
        ),

        titleLarge = TextStyle(
            fontFamily = fenwickWoodtype,
            fontSize = if(screenWidthDp < 600) 80.sp else 130.sp,
            shadow = Shadow(color = Color.Black),
            color = Color.White
        ),

        titleSmall = TextStyle(
            fontFamily = imFellEnglish,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    )
}
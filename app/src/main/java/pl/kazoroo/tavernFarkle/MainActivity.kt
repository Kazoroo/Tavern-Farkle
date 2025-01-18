package pl.kazoroo.tavernFarkle

import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PowerManager
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.android.gms.ads.MobileAds
import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository
import pl.kazoroo.tavernFarkle.core.presentation.navigation.Navigation
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.game.presentation.splashscreen.StartingScreenViewModel
import pl.kazoroo.tavernFarkle.game.service.MusicService
import pl.kazoroo.tavernFarkle.shop.data.OwnedSpecialDiceSerializer
import pl.kazoroo.tavernFarkle.shop.domain.AdManager
import pl.kazoroo.tavernFarkle.shop.domain.InventoryDataRepositoryImpl
import pl.kazoroo.tavernFarkle.ui.theme.DicesTheme

class MainActivity : ComponentActivity() {
    private lateinit var powerManager: PowerManager
    private val screenStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_ON -> SoundPlayer.setAppOnFocusState(true)
                Intent.ACTION_SCREEN_OFF -> SoundPlayer.setAppOnFocusState(false)
            }
        }
    }
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data")
    private val Context.protoDataStore by dataStore("dice_inventory.json", OwnedSpecialDiceSerializer)

    override fun onCreate(savedInstanceState: Bundle?) {
        showSplashScreen()
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        AdManager.loadRewardedAd(context = this)

        SoundPlayer.initialize(context = this)
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        SoundPlayer.setAppOnFocusState(powerManager.isInteractive)
        registerReceiver(screenStateReceiver, IntentFilter(Intent.ACTION_SCREEN_ON))
        registerReceiver(screenStateReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))

        setContent {
            DicesTheme {
                val userDataRepository = UserDataRepository(dataStore)
                val inventoryDataRepository = InventoryDataRepositoryImpl(protoDataStore)

                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    val intent = Intent(context, MusicService::class.java)
                    context.startService(intent)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(
                        userDataRepository,
                        inventoryDataRepository,
                        protoDataStore
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenStateReceiver)
        SoundPlayer.release()
    }

    override fun onPause() {
        super.onPause()
        stopService(Intent(this, MusicService::class.java))
        SoundPlayer.pauseAllSounds()
        SoundPlayer.setAppOnFocusState(false)
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        SoundPlayer.resumeAllSounds()
        SoundPlayer.setAppOnFocusState(true)
    }

    private fun showSplashScreen() {
        val startingScreenViewModel by viewModels<StartingScreenViewModel>()

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !startingScreenViewModel.isReady.value
            }
            setOnExitAnimationListener { screen ->
                val zoomX = ObjectAnimator.ofFloat(
                    screen.iconView, View.SCALE_X, 0.4f, 0.0f
                )
                zoomX.interpolator = OvershootInterpolator()
                zoomX.duration = 500L
                zoomX.doOnEnd { screen.remove() }

                val zoomY = ObjectAnimator.ofFloat(
                    screen.iconView, View.SCALE_Y, 0.4f, 0.0f
                )
                zoomY.interpolator = OvershootInterpolator()
                zoomY.duration = 500L
                zoomY.doOnEnd { screen.remove() }

                zoomX.start()
                zoomY.start()
            }
        }
    }
}
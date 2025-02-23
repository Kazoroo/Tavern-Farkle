package pl.kazoroo.tavernFarkle

import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.ads.MobileAds
import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository
import pl.kazoroo.tavernFarkle.core.presentation.navigation.Navigation
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.game.presentation.splashscreen.StartingScreenViewModel
import pl.kazoroo.tavernFarkle.game.service.MusicService
import pl.kazoroo.tavernFarkle.settings.presentation.SettingsViewModel
import pl.kazoroo.tavernFarkle.settings.presentation.SettingsViewModelFactory
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
    private lateinit var settingsViewModel: SettingsViewModel
    private var serviceBound = false
    private var musicService: MusicService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            settingsViewModel.initializeMusicService(musicService!!)
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
            musicService = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        showSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        AdManager.loadRewardedAd(context = this)

        SoundPlayer.initialize(context = applicationContext)
        powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        SoundPlayer.setAppOnFocusState(powerManager.isInteractive)
        registerReceiver(screenStateReceiver, IntentFilter(Intent.ACTION_SCREEN_ON))
        registerReceiver(screenStateReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))

        val context: Context = this
        val userDataRepository = UserDataRepository.getInstance(context)
        val inventoryDataRepository = InventoryDataRepositoryImpl.getInstance(context)

        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(userDataRepository)
        ).get(SettingsViewModel::class.java)

        val isMusicEnabled = settingsViewModel.loadMusicPreference()
        val musicServiceIntent = Intent(this, MusicService::class.java).apply {
            putExtra("MUSIC_ENABLED", isMusicEnabled)
        }
        startService(musicServiceIntent)
        bindService(musicServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        setContent {
            DicesTheme {
                LaunchedEffect(Unit) {
                    val intent = Intent(context, MusicService::class.java).apply {
                        putExtra("MUSIC_ENABLED", isMusicEnabled)
                    }
                    context.startService(intent)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(
                        userDataRepository,
                        inventoryDataRepository,
                        settingsViewModel
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(screenStateReceiver)
        SoundPlayer.release()
        if (serviceBound) {
            unbindService(serviceConnection)
        }
    }

    override fun onPause() {
        super.onPause()
        musicService?.pauseMusic()
        SoundPlayer.pauseAllSounds()
        SoundPlayer.setAppOnFocusState(false)
    }

    override fun onResume() {
        super.onResume()
        if (settingsViewModel.loadMusicPreference()) {
            musicService?.resumeMusic()
        }
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
package pl.kazoroo.tavernFarkle

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.PowerManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.ads.MobileAds
import pl.kazoroo.tavernFarkle.core.presentation.CoinsViewModel
import pl.kazoroo.tavernFarkle.core.presentation.navigation.Navigation
import pl.kazoroo.tavernFarkle.di.DependencyContainer
import pl.kazoroo.tavernFarkle.di.TavernFarkleApp
import pl.kazoroo.tavernFarkle.menu.presentation.MainMenuViewModel
import pl.kazoroo.tavernFarkle.menu.sound.MusicService
import pl.kazoroo.tavernFarkle.menu.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.menu.splashscreen.StartingScreenViewModel
import pl.kazoroo.tavernFarkle.multiplayer.presentation.LobbyViewModel
import pl.kazoroo.tavernFarkle.settings.presentation.SettingsViewModel
import pl.kazoroo.tavernFarkle.shop.domain.AdManager
import pl.kazoroo.tavernFarkle.shop.presentation.inventory.InventoryViewModel
import pl.kazoroo.tavernFarkle.ui.theme.DicesTheme

class MainActivity : ComponentActivity() {
    private val dependencyContainer: DependencyContainer
        get() = (application as TavernFarkleApp).dependencyContainer

    private val settingsViewModel by viewModels<SettingsViewModel>(
        factoryProducer = { dependencyContainer.settingsViewModelFactory }
    )

    private lateinit var powerManager: PowerManager
    private val screenStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_ON -> SoundPlayer.setAppOnFocusState(true)
                Intent.ACTION_SCREEN_OFF -> SoundPlayer.setAppOnFocusState(false)
            }
        }
    }
    private var serviceBound = false
    private var musicService: MusicService? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.LocalBinder
            musicService = binder.getService()
            settingsViewModel.initializeMusicService(musicService!!)
            serviceBound = true

            if(
                settingsViewModel.loadMusicPreference() &&
                lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            ) {
                musicService?.resumeMusic()
            }
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
        powerManager = getSystemService(POWER_SERVICE) as PowerManager
        SoundPlayer.setAppOnFocusState(powerManager.isInteractive)
        registerReceiver(screenStateReceiver, IntentFilter(Intent.ACTION_SCREEN_ON))
        registerReceiver(screenStateReceiver, IntentFilter(Intent.ACTION_SCREEN_OFF))

        val isMusicEnabled = settingsViewModel.loadMusicPreference()
        val musicServiceIntent = Intent(this, MusicService::class.java).apply {
            putExtra("MUSIC_ENABLED", isMusicEnabled)
        }
        bindService(musicServiceIntent, serviceConnection, BIND_AUTO_CREATE)

        setContent {
            val mainMenuViewModel = viewModel<MainMenuViewModel>(
                factory = dependencyContainer.mainMenuViewModelFactory
            )
            val coinsMenuViewModel = viewModel<CoinsViewModel>(
                factory = dependencyContainer.coinsViewModelFactory
            )
            val inventoryViewModel = viewModel<InventoryViewModel>(
                factory = dependencyContainer.inventoryViewModelFactory
            )
            val lobbyViewModel = viewModel<LobbyViewModel>(
                factory = dependencyContainer.lobbyViewModelFactory
            )

            DicesTheme {
                enableEdgeToEdge()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(
                        mainMenuViewModel = mainMenuViewModel,
                        coinsViewModel = coinsMenuViewModel,
                        inventoryViewModel = inventoryViewModel,
                        settingsViewModel = settingsViewModel,
                        dependencyContainer = dependencyContainer,
                        lobbyViewModel = lobbyViewModel
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
                screen.view
                    .animate()
                    .alpha(0f)
                    .withEndAction { screen.remove() }
                    .start()
            }
        }
    }
}
package pl.kazoroo.tavernFarkle.settings.presentation

import androidx.lifecycle.ViewModel
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.game.service.MusicService
import java.lang.ref.WeakReference

class SettingsViewModel: ViewModel() {
    private var musicService: WeakReference<MusicService>? = null

    fun setSoundEnabled(isEnabled: Boolean) {
        SoundPlayer.setIsStateEnabled(isEnabled)
    }

    fun setMusicService(service: MusicService) {
        this.musicService = WeakReference(service)
    }

    fun togglePlayback(shouldMusicPlay: Boolean) {
        if (shouldMusicPlay) {
            musicService?.get()?.resumeMusic()
        } else {
            musicService?.get()?.pauseMusic()
        }
    }
}
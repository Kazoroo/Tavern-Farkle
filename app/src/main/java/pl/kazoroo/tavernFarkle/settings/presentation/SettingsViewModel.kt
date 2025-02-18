package pl.kazoroo.tavernFarkle.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey
import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository
import pl.kazoroo.tavernFarkle.core.domain.SaveUserDataUseCase
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.game.service.MusicService
import java.lang.ref.WeakReference

class SettingsViewModel(
    userDataRepository: UserDataRepository
): ViewModel() {
    private var musicService: WeakReference<MusicService>? = null
    private val saveUserDataUseCase = SaveUserDataUseCase(userDataRepository)

    fun initializeMusicService(service: MusicService) {
        this.musicService = WeakReference(service)
    }

    fun setIsSoundEnabledState(isEnabled: Boolean) {
        viewModelScope.launch {
            SoundPlayer.setIsStateEnabled(isEnabled)
            saveUserDataUseCase.invoke(isEnabled, UserDataKey.IS_SOUND_ENABLED)
        }
    }

    fun togglePlayback(shouldMusicPlay: Boolean) {
        viewModelScope.launch {
            if (shouldMusicPlay) {
                musicService?.get()?.resumeMusic()
                saveUserDataUseCase.invoke(true, UserDataKey.IS_MUSIC_ENABLED)
            } else {
                musicService?.get()?.pauseMusic()
                saveUserDataUseCase.invoke(false, UserDataKey.IS_MUSIC_ENABLED)
            }
        }
    }
}
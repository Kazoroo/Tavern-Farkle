package pl.kazoroo.tavernFarkle.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey
import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository
import pl.kazoroo.tavernFarkle.core.domain.ReadUserDataUseCase
import pl.kazoroo.tavernFarkle.core.domain.SaveUserDataUseCase
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundPlayer
import pl.kazoroo.tavernFarkle.game.service.MusicService
import java.lang.ref.WeakReference

class SettingsViewModel(
    userDataRepository: UserDataRepository
): ViewModel() {
    private var musicService: WeakReference<MusicService>? = null
    private val saveUserDataUseCase = SaveUserDataUseCase(userDataRepository)
    private val readUserDataUseCase = ReadUserDataUseCase(userDataRepository)

    init {
        SoundPlayer.setIsEnabledFlag(loadSoundPreference())
    }

    fun initializeMusicService(service: MusicService) {
        this.musicService = WeakReference(service)
    }

    fun setSoundState(isSoundEnabled: Boolean) {
        viewModelScope.launch {
            SoundPlayer.setIsEnabledFlag(isSoundEnabled)
            saveUserDataUseCase.invoke(isSoundEnabled, UserDataKey.IS_SOUND_ENABLED)
        }
    }

    fun togglePlayback(isMusicEnabled: Boolean) {
        viewModelScope.launch {
            if (isMusicEnabled) {
                musicService?.get()?.resumeMusic()
                saveUserDataUseCase.invoke(true, UserDataKey.IS_MUSIC_ENABLED)
            } else {
                musicService?.get()?.pauseMusic()
                saveUserDataUseCase.invoke(false, UserDataKey.IS_MUSIC_ENABLED)
            }
        }
    }

    fun loadSoundPreference(): Boolean {
        return readUserDataUseCase.invoke(UserDataKey.IS_SOUND_ENABLED)
    }

    fun loadMusicPreference(): Boolean {
        return readUserDataUseCase.invoke(UserDataKey.IS_MUSIC_ENABLED)
    }
}
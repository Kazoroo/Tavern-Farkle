package pl.kazoroo.tavernFarkle.settings.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pl.kazoroo.tavernFarkle.core.data.local.UserDataKey
import pl.kazoroo.tavernFarkle.core.data.local.repository.UserDataRepository
import pl.kazoroo.tavernFarkle.core.domain.usecase.userdata.ReadUserDataUseCase
import pl.kazoroo.tavernFarkle.core.domain.usecase.userdata.SaveUserDataUseCase
import pl.kazoroo.tavernFarkle.menu.sound.MusicService
import pl.kazoroo.tavernFarkle.menu.sound.SoundPlayer
import java.lang.ref.WeakReference

class SettingsViewModel(
    userDataRepository: UserDataRepository
): ViewModel() {
    private var musicService: WeakReference<MusicService>? = null
    private val saveUserDataUseCase = SaveUserDataUseCase(userDataRepository)
    private val readUserDataUseCase = ReadUserDataUseCase(userDataRepository)

    private val _isSoundEnabled = mutableStateOf(true)
    val isSoundEnabled: State<Boolean> = _isSoundEnabled

    init {
        val soundPreference = loadSoundPreference()
        SoundPlayer.setIsEnabledFlag(soundPreference)

        _isSoundEnabled.value = soundPreference
    }

    fun initializeMusicService(service: MusicService) {
        this.musicService = WeakReference(service)
    }

    fun setSoundState(isSoundEnabled: Boolean) {
        viewModelScope.launch {
            SoundPlayer.setIsEnabledFlag(isSoundEnabled)
            saveUserDataUseCase.invoke(isSoundEnabled, UserDataKey.IS_SOUND_ENABLED)
            _isSoundEnabled.value = isSoundEnabled
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

    private fun loadSoundPreference(): Boolean {
        return readUserDataUseCase.invoke(UserDataKey.IS_SOUND_ENABLED)
    }

    fun loadMusicPreference(): Boolean {
        return readUserDataUseCase.invoke(UserDataKey.IS_MUSIC_ENABLED)
    }
}
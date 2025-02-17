package pl.kazoroo.tavernFarkle.settings.presentation

import androidx.lifecycle.ViewModel
import pl.kazoroo.tavernFarkle.game.presentation.sound.SoundPlayer

class SettingsViewModel: ViewModel() {
    fun setSoundEnabled(isEnabled: Boolean) {
        SoundPlayer.setIsStateEnabled(isEnabled)
    }
}
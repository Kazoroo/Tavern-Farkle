package pl.kazoroo.tavernFarkle.game.presentation.sound

import android.content.Context
import android.media.SoundPool
import pl.kazoroo.tavernFarkle.R

object SoundPlayer {
    private val soundPool: SoundPool = SoundPool.Builder().setMaxStreams(5).build()
    private lateinit var soundMap: Map<SoundType, List<Int>>
    private var isAppOnFocus = true
    private val activeSounds = mutableListOf<Int>()
    private var isSoundEnabled = true

    fun setAppOnFocusState(isOn: Boolean) {
        isAppOnFocus = isOn
    }

    fun initialize(context: Context) {
        soundMap = mapOf(
            SoundType.CLICK to listOf(soundPool.load(context, R.raw.click_sound, 1)),
            SoundType.WIN to listOf(soundPool.load(context, R.raw.win_sound, 1)),
            SoundType.FAILURE to listOf(soundPool.load(context, R.raw.failure_sound, 1)),
            SoundType.SKUCHA to listOf(soundPool.load(context, R.raw.skucha_sound, 1)),
            SoundType.DICE_ROLLING to listOf(
                soundPool.load(context, R.raw.dice_rolling, 1),
                soundPool.load(context, R.raw.dice_rolling2, 1),
                soundPool.load(context, R.raw.shaking_and_rolling_dice, 1)
            )
        )
    }

    fun playSound(type: SoundType) {
        if(isAppOnFocus && isSoundEnabled) {
            soundMap[type]?.let { sounds ->
                val soundId = sounds.random()
                val streamId = soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                if (streamId != 0) {
                    activeSounds.add(streamId)
                }
            }
        }
    }

    fun setIsStateEnabled(state: Boolean) {
        isSoundEnabled = state
    }

    fun pauseAllSounds() {
        activeSounds.forEach { soundPool.pause(it) }
    }

    fun resumeAllSounds() {
        activeSounds.forEach { soundPool.resume(it) }
    }

    fun release() {
        soundPool.release()
    }
}

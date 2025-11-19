package pl.kazoroo.tavernFarkle.menu.sound

import android.content.Context
import android.media.SoundPool
import pl.kazoroo.tavernFarkle.R

object SoundPlayer {
    private var soundPool: SoundPool? = null
    private lateinit var soundMap: Map<SoundType, List<Int>>
    private var isAppOnFocus = true
    private val activeSounds = mutableListOf<Int>()
    private var isSoundEnabled = true

    fun setAppOnFocusState(isOn: Boolean) {
        isAppOnFocus = isOn
    }

    fun initialize(context: Context) {
        val appContext = context.applicationContext
        soundPool = SoundPool.Builder().setMaxStreams(5).build()
        soundMap = mapOf(
            SoundType.CLICK to listOf(soundPool!!.load(appContext, R.raw.click_sound, 1)),
            SoundType.WIN to listOf(soundPool!!.load(appContext, R.raw.win_sound, 1)),
            SoundType.FAILURE to listOf(soundPool!!.load(appContext, R.raw.failure_sound, 1)),
            SoundType.SKUCHA to listOf(soundPool!!.load(appContext, R.raw.skucha_sound, 1)),
            SoundType.DICE_ROLLING to listOf(
                soundPool!!.load(appContext, R.raw.dice_rolling, 1),
                soundPool!!.load(appContext, R.raw.dice_rolling2, 1),
                soundPool!!.load(appContext, R.raw.shaking_and_rolling_dice, 1)
            ),
            SoundType.FALLING_COINS to listOf(
                soundPool!!.load(appContext, R.raw.coin_sound_1, 1),
                soundPool!!.load(appContext, R.raw.coin_sound_2, 1),
                soundPool!!.load(appContext, R.raw.coin_sound_3, 1),
            )
        )
    }

    fun playSound(type: SoundType) {
        if(isAppOnFocus && isSoundEnabled) {
            soundMap[type]?.let { sounds ->
                val soundId = sounds.random()
                soundPool?.let {
                    val streamId = it.play(soundId, 1f, 1f, 1, 0, 1f)
                    if (streamId != 0) {
                        activeSounds.add(streamId)
                    }
                }
            }
        }
    }

    fun setIsEnabledFlag(state: Boolean, context: Context? = null) {
        isSoundEnabled = state

        val soundsIncorrectlyInitialized = state && soundMap.values.flatten().all { it == 0 } && context != null
        if (soundsIncorrectlyInitialized) {
            initialize(context)
        }
    }

    fun pauseAllSounds() {
        activeSounds.forEach { soundPool?.pause(it) }
    }

    fun resumeAllSounds() {
        activeSounds.forEach { soundPool?.resume(it) }
    }

    fun release() {
        soundPool?.release()
    }
}
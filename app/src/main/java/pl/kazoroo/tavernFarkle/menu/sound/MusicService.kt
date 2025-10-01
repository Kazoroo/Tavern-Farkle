package pl.kazoroo.tavernFarkle.menu.sound

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import pl.kazoroo.tavernFarkle.R

class MusicService : Service() {
    private lateinit var mediaPlayer: MediaPlayer
    private val musicFiles = listOf(R.raw.tavern, R.raw.kempps, R.raw.arabish, R.raw.sonnical, R.raw.inki, R.raw.cowboy)
    private var isPaused = false
    private var musicEnabled = true

    inner class LocalBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private fun playRandomSong() {
        if(::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }

        val randomResId = musicFiles.random()

        mediaPlayer = MediaPlayer.create(this, randomResId).apply {
            start()
            isPaused = false
            setOnCompletionListener {
                playRandomSong()
            }
        }
    }

    fun pauseMusic() {
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            isPaused = true
        }
    }

    fun resumeMusic() {
        if (!::mediaPlayer.isInitialized) {
            playRandomSong()
        } else if (isPaused) {
            mediaPlayer.start()
            isPaused = false
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            musicEnabled = it.getBooleanExtra("MUSIC_ENABLED", true)
        }
        if (musicEnabled) {
            if (!::mediaPlayer.isInitialized || !mediaPlayer.isPlaying) {
                playRandomSong()
            }
        } else {
            if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                pauseMusic()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder {
        return LocalBinder()
    }
}
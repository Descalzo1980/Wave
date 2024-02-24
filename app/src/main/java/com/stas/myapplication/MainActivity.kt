package com.stas.myapplication


import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.stas.myapplication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), Runnable {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPlayer: MediaPlayer
    private var wasPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fab = binding.button

        fab.setOnClickListener {
            playSong()
        }
    }

    private fun playSong() {
        try {
            if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
                clearMediaPlayer()
                wasPlaying = true
                binding.button.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_message_audio_file_play))
            } else {
                if (!::mediaPlayer.isInitialized) {
                    mediaPlayer = MediaPlayer()
                    val baseVisualizer = binding.visualizer
                    baseVisualizer.setColor(ContextCompat.getColor(this, R.color.random_color))
                    baseVisualizer.setDensity(70f)
                    val assetFileDescriptor: AssetFileDescriptor = assets.openFd("beton.mp3")
                    mediaPlayer.setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
                    mediaPlayer.setOnCompletionListener {
                        clearMediaPlayer()
                        binding.button.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_message_audio_file_play))
                    }
                    baseVisualizer.setPlayer(mediaPlayer.audioSessionId)
                }
                binding.button.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_message_audio_file_pause))
                mediaPlayer.prepare()
                mediaPlayer.setVolume(0.5f, 0.5f)
                mediaPlayer.isLooping = false
                mediaPlayer.start()
                Thread(this@MainActivity).start()
                wasPlaying = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun run() {
        while (mediaPlayer.isPlaying) {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                return
            } catch (e: Exception) {
                return
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()
    }

    private fun clearMediaPlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }
}

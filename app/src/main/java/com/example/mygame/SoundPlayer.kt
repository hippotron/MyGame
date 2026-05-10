package com.example.mygame

import android.content.Context
import android.media.MediaPlayer
import android.os.Looper
import java.util.logging.Handler

/**
 * Максимально простой проигрыватель звуков
 */
class SoundPlayer(val context: Context) {

    private var mp: MediaPlayer? = null
    private val soundCache = mutableMapOf<Int, MediaPlayer>()

    init {
        preload(R.raw.start_game)
        preload(R.raw.hod)
        preload(R.raw.button)
        preload(R.raw.victory)
    }

    fun preload(vararg rawResIds: Int) {
        for (id in rawResIds) {
            if (!soundCache.containsKey(id)) {
                soundCache[id] = MediaPlayer.create(context, id)
            }
        }
    }

    fun play(
        rawResId: Int,
        loop: Boolean = false,
        volume: Float = 1.0f
    ) {
        // Останавливаем предыдущий MediaPlayer если есть
        releaseCurrent()

        var mp = soundCache[rawResId]

        if (mp == null) {
            mp = MediaPlayer.create(context, rawResId)
            soundCache[rawResId] = mp
        }

        mp.setVolume(volume, volume)

        // Если звук уже играет, перематываем в начало
        if (mp.isPlaying) {
            mp.pause()
            mp.seekTo(0)
        }

        mp.isLooping = loop

        // Устанавливаем слушатель завершения
        mp.setOnCompletionListener {
            // Если звук не зациклен, удаляем из кэша и освобождаем
            if (!loop) {
                soundCache.remove(rawResId)?.release()
            }
        }

        mp.start()
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            // Ваше событие через 2 секунды
            println("Событие выполнено")
        }, 1000) // задержка в миллисекундах
    }

    fun stop(rawResId: Int? = null) {
        if (rawResId != null) {
            soundCache[rawResId]?.stop()
        } else {
            soundCache.values.forEach { it.stop() }
        }
    }

    private fun releaseCurrent() {
        // Освобождаем только неиспользуемые звуки, которые уже завершились
        val iterator = soundCache.iterator()
        while (iterator.hasNext()) {
            val (id, player) = iterator.next()
            if (!player.isPlaying) {
                player.release()
                iterator.remove()
            }
        }
    }

    fun release() {
        soundCache.values.forEach { it.release() }
        soundCache.clear()
    }
}
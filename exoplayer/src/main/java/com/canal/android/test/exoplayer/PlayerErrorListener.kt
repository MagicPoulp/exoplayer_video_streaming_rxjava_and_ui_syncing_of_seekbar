package com.canal.android.test.exoplayer

import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.canal.android.test.exoplayer.exception.CustomExoplayerException
import com.canal.android.test.exoplayer.exception.ExoplayerException

@UnstableApi class PlayerErrorListener(
    private val onError: (ExoplayerException) -> Unit
) : AnalyticsListener {
    // Question 1.8 Handle exoplayer onPlayerError in order to display a simple dialog in ui when happening
    override fun onPlayerError(eventTime: AnalyticsListener.EventTime, error: PlaybackException) {
        val message = error.message ?: error.errorCodeName
        onError(CustomExoplayerException(message, null))
    }
}
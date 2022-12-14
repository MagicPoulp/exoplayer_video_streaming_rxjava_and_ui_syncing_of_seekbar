package com.canal.android.test.exoplayer

import androidx.media3.common.PlaybackException
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.canal.android.test.exoplayer.exception.ExoplayerException

@UnstableApi class PlayerErrorListener(
    private val onError: (ExoplayerException) -> Unit
) : AnalyticsListener {
    override fun onPlayerError(eventTime: AnalyticsListener.EventTime, error: PlaybackException) {
        // TODO: dispatch all player error with exoplayer error codes to user
    }
}
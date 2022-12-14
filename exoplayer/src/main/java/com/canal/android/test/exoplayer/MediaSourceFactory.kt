package com.canal.android.test.exoplayer

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.drm.DrmSessionManager
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import com.canal.android.test.exoplayer.exception.ManifestTypeUnknown

@UnstableApi object MediaSourceFactory {

    /**
     * Build stream media source
     *
     */
    @Throws(ManifestTypeUnknown::class)
    fun buildMediaSource(
        uri: Uri,
        dataDataSource: DataSource.Factory,
        drmSessionManager: DrmSessionManager? = null
    ): MediaSource {
        return when (@C.ContentType val type = Util.inferContentType(uri)) {
            C.CONTENT_TYPE_DASH -> buildDashMediaSource(dataDataSource, uri, drmSessionManager)
            C.CONTENT_TYPE_HLS -> buildHlsMediaSource(dataDataSource, uri)
            else -> {
                throw ManifestTypeUnknown("Unsupported type: $type")
            }
        }
    }

    private fun buildDashMediaSource(
        dataDataSource: DataSource.Factory,
        uri: Uri,
        drmSessionManager: DrmSessionManager?
    ): MediaSource {
        val mediaSourceFactory =
            DashMediaSource.Factory(dataDataSource)
        if (drmSessionManager != null) mediaSourceFactory.setDrmSessionManagerProvider { drmSessionManager }
        return mediaSourceFactory
            .createMediaSource(
                MediaItem.Builder()
                    .setUri(uri)
                    .build()
            )
    }

    private fun buildHlsMediaSource(
        dataDataSource: DataSource.Factory,
        uri: Uri
    ): MediaSource = HlsMediaSource.Factory(dataDataSource)
        .createMediaSource(
            MediaItem.Builder()
                .setUri(uri)
                .build()
        )
}
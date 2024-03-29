package com.canal.android.test.exoplayer

import android.view.SurfaceView
import com.canal.android.test.common.PlayerRatio
import com.canal.android.test.exoplayer.model.PlayerState
import io.reactivex.Completable
import io.reactivex.Observable

interface PlayerCore {
    fun isPaused(): Boolean
    fun pause(): Completable
    fun togglePlayPause(): Completable
    fun stop(): Completable
    fun release(): Completable
    fun seekTo(positionMs: Long): Completable
    fun selectTrack(trackType: Int, trackGroupIndex: Int, trackIndex: Int): Completable
    fun setPlayerView(surfaceView: SurfaceView): Completable

    fun startPlayback(manifestUrl: String, callbackOnVideoSizeChanged: ((PlayerRatio) -> Unit)?, seekToPositionMs: Long?): Observable<PlayerState>
}
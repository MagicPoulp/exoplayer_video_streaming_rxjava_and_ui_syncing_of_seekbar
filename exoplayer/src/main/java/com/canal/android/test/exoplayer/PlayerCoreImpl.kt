package com.canal.android.test.exoplayer

import android.content.Context
import android.net.Uri
import android.os.Looper
import android.view.SurfaceView
import androidx.media3.common.Player.STATE_BUFFERING
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_IDLE
import androidx.media3.common.Player.STATE_READY
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import com.canal.android.test.common.PlayerRatio
import com.canal.android.test.exoplayer.model.PlayerState
import com.canal.android.test.exoplayer.model.PlayerStatus
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

@UnstableApi class PlayerCoreImpl(
    context: Context
) : PlayerCore {

    private val rendererFactory = DefaultRenderersFactory(context)
    private val player: ExoPlayer = ExoPlayer.Builder(context, rendererFactory)
        .setLooper(Looper.getMainLooper())
        .build()

    private val _stateSubject: BehaviorSubject<PlayerState> = BehaviorSubject.create()
    internal val currentState: PlayerState
        get() = _stateSubject.value ?: PlayerState()

    private val compositeDisposable = CompositeDisposable()

    private val errorListener: PlayerErrorListener by lazy {
        PlayerErrorListener { exception -> _stateSubject.onError(exception) }
    }

    private var callbackOnVideoSizeChanged: ((PlayerRatio) -> Unit)? = null

    private val listener: AnalyticsListener by lazy {
        object : AnalyticsListener {

            override fun onPlayWhenReadyChanged(
                eventTime: AnalyticsListener.EventTime,
                playWhenReady: Boolean,
                reason: Int,
            ) {
                super.onPlayWhenReadyChanged(eventTime, playWhenReady, reason)
                if (player.playbackState == STATE_READY) {
                    when (player.playWhenReady) {
                        true -> PlayerStatus.PLAYING
                        false -> PlayerStatus.PAUSED
                    }.also { _stateSubject.onNext(currentState.copy(status = it)) }
                }
            }

            override fun onPlaybackStateChanged(
                eventTime: AnalyticsListener.EventTime,
                state: Int
            ) {
                when (state) {
                    STATE_BUFFERING -> _stateSubject.onNext(
                        currentState.copy(status = PlayerStatus.BUFFERING)
                    )

                    STATE_READY -> {
                        when (player.playWhenReady) {
                            true -> PlayerStatus.PLAYING
                            false -> PlayerStatus.PAUSED
                        }.also { _stateSubject.onNext(currentState.copy(status = it)) }
                    }

                    STATE_ENDED,
                    STATE_IDLE,
                    -> _stateSubject.onNext(
                        currentState.copy(status = PlayerStatus.IDLE)
                    )
                }
            }

            override fun onVideoSizeChanged(
                eventTime: AnalyticsListener.EventTime,
                videoSize: VideoSize
            ) {
                super.onVideoSizeChanged(eventTime, videoSize)
                callbackOnVideoSizeChanged?.let { it(PlayerRatio(videoSize.width, videoSize.height)) }
            }
        }
    }

    private fun startPlayerPositionUpdate() {
        Observable.interval(1L, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                currentState.copy(
                    position = player.currentPosition,
                    durationMs = player.duration
                )
            }
            .subscribe({
                _stateSubject.onNext(it)
            }, {
                _stateSubject.onError(it)
            }).also {
                compositeDisposable.add(it)
            }
    }

    override fun setPlayerView(surfaceView: SurfaceView): Completable =
        Completable.fromAction {
            player.setVideoSurfaceView(surfaceView)
        }.subscribeOn(AndroidSchedulers.mainThread())

    override fun startPlayback(manifestUrl: String, callbackOnVideoSizeChanged: ((PlayerRatio) -> Unit)?): Observable<PlayerState> =
        Single.fromCallable {
            MediaSourceFactory.buildMediaSource(
                Uri.parse(manifestUrl),
                DefaultHttpDataSource.Factory()
            )
        }.flatMapCompletable { mediaSource ->
            Completable.fromAction {
                this.callbackOnVideoSizeChanged = callbackOnVideoSizeChanged
                player.addAnalyticsListener(listener)
                player.addAnalyticsListener(errorListener)
                player.setMediaSource(mediaSource)
                player.playWhenReady = true
                player.prepare()
                startPlayerPositionUpdate()
            }.subscribeOn(AndroidSchedulers.mainThread())
        }
            .andThen(_stateSubject)
            .subscribeOn(AndroidSchedulers.mainThread())

    override fun pause(): Completable = Completable.complete() // TODO: implement pause

    override fun togglePlayPause(): Completable = Completable.complete() // TODO: implement pause

    override fun stop(): Completable {
        player.stop()
        compositeDisposable.clear()
        return Completable.complete()
    }

    override fun release(): Completable {
        player.release()
        compositeDisposable.dispose()
        return Completable.complete()
    }

    override fun seekTo(positionMs: Long): Completable = Completable.complete() // TODO: seekTo

    override fun selectTrack(trackType: Int, trackGroupIndex: Int, trackIndex: Int): Completable =
        Completable.complete() // TODO: selectTrack (video/audio/text)

    companion object {
        fun getInstance(
            context: Context
        ): PlayerCoreImpl {
            return PlayerCoreImpl(
                context = context
            )
        }
    }
}
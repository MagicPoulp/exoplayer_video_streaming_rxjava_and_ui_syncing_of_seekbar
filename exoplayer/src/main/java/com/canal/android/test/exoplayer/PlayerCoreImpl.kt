package com.canal.android.test.exoplayer

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.SurfaceView
import androidx.media3.common.Player.STATE_BUFFERING
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_IDLE
import androidx.media3.common.Player.STATE_READY
import androidx.media3.common.TrackSelectionOverride
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
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import org.reactivestreams.Subscription
import java.util.concurrent.TimeUnit


@UnstableApi class PlayerCoreImpl(
    context: Context
) : PlayerCore {

    private val rendererFactory = DefaultRenderersFactory(context)
    // we use an adapter class because COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM is missing for seekTo()
    private val player: ExoPlayer = CustomExoPlayerImpl(ExoPlayer.Builder(context, rendererFactory)
        .setLooper(Looper.getMainLooper())
        .build())

    private val _stateSubject: BehaviorSubject<PlayerState> = BehaviorSubject.create()
    internal val currentState: PlayerState
        get() = _stateSubject.value ?: PlayerState()

    private val compositeDisposable = CompositeDisposable()

    private val errorListener: PlayerErrorListener by lazy {
        PlayerErrorListener { exception ->  _stateSubject.onError(exception) }
    }

    private var callbackOnVideoSizeChanged: ((PlayerRatio) -> Unit)? = null

    private var longDisposable1: Disposable? = null

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
        println("DB startPlayerPositionUpdate start")
        // Completable.fromObservable(, toOb
        val disposable = Observable.interval(1L, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                println("DB new")
                currentState.copy(
                    position = player.currentPosition,
                    durationMs = player.duration
                )
            }
            .doOnSubscribe { player.play() }
            .doOnDispose { println("DB DISPOSE")
                Handler(Looper.getMainLooper()).post { player.release() } }
            .subscribe({
                println("DB new2")
                _stateSubject.onNext(it)
            }, {
                _stateSubject.onError(it)
            })
        compositeDisposable.add(disposable)
    }

    override fun setPlayerView(surfaceView: SurfaceView): Completable =
        Completable.fromAction {
            player.setVideoSurfaceView(surfaceView)
        }.subscribeOn(AndroidSchedulers.mainThread())

    override fun startPlayback(manifestUrl: String, callbackOnVideoSizeChanged: ((PlayerRatio) -> Unit)?, seekToPositionMs: Long?): Observable<PlayerState> =
        Single.fromCallable {
            MediaSourceFactory.buildMediaSource(
                Uri.parse(manifestUrl),
                DefaultHttpDataSource.Factory()
            )
        }.flatMapCompletable { mediaSource ->
            // NOTE: it was changed to a single to avoid being repeated for 2 subscription
            // we need a new subscription so that we can have a disposable and cancel the loop every second
            // in startPlayerPositionUpdate
            val completable1 = Single.fromCallable {
                // player.release() // this can be useful to debug disposables
                this.callbackOnVideoSizeChanged = callbackOnVideoSizeChanged
                player.addAnalyticsListener(listener)
                player.addAnalyticsListener(errorListener)
                player.setMediaSource(mediaSource)
                player.prepare()
                seekToPositionMs?.let {
                    // we must use 2 parameters because we have COMMAND_SEEK_TO_MEDIA_ITEM but not COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM
                    // see the source code documentation for details
                    // However, it enforces the first mediaItemIndex
                    // a wrapper class was used for the player to use seekTo with the missing command COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM
                    player.seekTo(seekToPositionMs)
                }
                player.playWhenReady = false
                println("DB PLAYER SETUP")
                startPlayerPositionUpdate()
            }.subscribeOn(AndroidSchedulers.mainThread())
            //    .doOnDispose { println("DB DISPOSE2") }
            val localDisposable1 = completable1.subscribe()
            localDisposable1?.let { compositeDisposable.add(it) }
            longDisposable1 = localDisposable1
            println("DB STEP 3")
            Completable.complete()
        }
            .doOnDispose { println("DB DISPOSE4") }
            .andThen(_stateSubject)
            .subscribeOn(AndroidSchedulers.mainThread())

    override fun pause(): Completable {
        player.pause()
        return Completable.complete()
    }

    private fun play(): Completable {
        startPlayerPositionUpdate()
        player.play()
        return Completable.complete()
    }

    override fun isPaused(): Boolean {
        return currentState.status == PlayerStatus.PAUSED
    }

    override fun togglePlayPause(): Completable {
        return when (currentState.status) {
            PlayerStatus.IDLE -> play()
            PlayerStatus.PLAYING -> pause()
            PlayerStatus.PAUSED -> play()
            PlayerStatus.BUFFERING -> pause()
            else -> Completable.complete()
        }
    }

    override fun stop(): Completable {
        player.stop()
        player.release()
        compositeDisposable.clear()
        return Completable.complete()
    }

    override fun release(): Completable {
        println("DB EXO RELEASE")
        compositeDisposable.dispose()
        //player.playWhenReady = false
        //player.pause()
        //player.stop()
        player.release()
        return Completable.complete()
    }

    override fun seekTo(positionMs: Long): Completable {
        pause()
        // a wrapper class was used for the player to use seekTo with the missing command COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM
        player.seekTo(positionMs)
        play()
        return Completable.complete()
    }

    // https://exoplayer.dev/track-selection.html
    override fun selectTrack(trackType: Int, trackGroupIndex: Int, trackIndex: Int): Completable {
        player.pause()
        val tracks = player.currentTracks
        if (trackGroupIndex >= tracks.groups.size) {
            return Completable.complete()
        }
        val trackGroup = tracks.groups[trackGroupIndex]
        val groupTrackType: Int = trackGroup.type
        val trackInGroupIsSupported = trackGroup.isSupported
        if (!trackInGroupIsSupported || trackType != groupTrackType || trackIndex >= trackGroup.length) {
            return Completable.complete()
        }
        val isTrackSupported = trackGroup.isTrackSupported(trackIndex)
        val isTrackSelected = trackGroup.isTrackSelected(trackIndex)
        if (isTrackSupported && !isTrackSelected) {
            player.trackSelectionParameters = player.trackSelectionParameters
                .buildUpon()
                .setOverrideForType(
                    TrackSelectionOverride(
                        trackGroup.mediaTrackGroup,
                        0
                    )
                )
                .build()
            player.play()
            return Completable.complete()
        }
        return Completable.complete()
    }

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
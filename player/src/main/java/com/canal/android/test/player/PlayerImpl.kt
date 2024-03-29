package com.canal.android.test.player

import android.content.Context
import android.util.Log
import android.view.SurfaceView
import com.canal.android.test.common.PlayerRatio
import com.canal.android.test.common.PositionState
import com.canal.android.test.exoplayer.PlayerCoreImpl
import com.canal.android.test.exoplayer.PlayerFactory
import com.canal.android.test.player.model.PlayerAction
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.ReplaySubject

class PlayerImpl(
    private val context: Context
) : Player {

    override val playerView: SurfaceView by lazy { SurfaceView(context) }

    override fun pushAction(action: PlayerAction) = playerActionSubject.onNext(action)

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val playerSubject: BehaviorSubject<PlayerCoreImpl> = BehaviorSubject.create()

    private val playerActionSubject: ReplaySubject<PlayerAction> =
        ReplaySubject.createWithSize(PLAYER_ACTION_BUFFER_SIZE)
    private val playerActionObservable: Observable<PlayerAction>
        get() = playerActionSubject

    private var blockRestart: Boolean = false

    init {
        observePlayerActions()
        initializePlayer()
            // We set the main thread here because the player commands must be run on the main
            // thread. It depends on how the ExoPlayer was instantiated with a Looper.
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ player ->
                playerSubject.onNext(player)
            }, { throwable ->
                Log.e(TAG, "error", throwable)
            }).autoDispose()
    }

    private fun observePlayerActions() {
        compositeDisposable.add(playerActionObservable
            .flatMapCompletable { action ->
                action.handle()
            }
            // Question 1.3 the player must be used from the same thread has the looper set in PlayerCoreImpl
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe())
    }

    // Question 1.1 implement all remaining PlayerAction
    private fun PlayerAction.handle(): Completable =
        if (blockRestart) {
            Completable.complete()
        }
        else when (this) {
            is PlayerAction.PlayPauseClicked -> Completable.complete()
            is PlayerAction.Play -> play()
            is PlayerAction.Pause -> pause()
            is PlayerAction.SeekTo -> seekTo(this.seekToPositionMs)
            is PlayerAction.StopPlayback -> stopPlayback()
            is PlayerAction.Release -> releasePlayer(this.blockRestart)
            is PlayerAction.SelectTrack -> selectTrack(this.trackType, this.trackGroupIndex, this.trackIndex)
            is PlayerAction.StartPlayback -> startPlayback(this.manifestUrl, this.callbackOnVideoSizeChanged,
                this.seekToPositionMs, this.callbackOnPositionStateChanged, this.callbackOnError)
            else -> Completable.complete()
        }

    private fun play(): Completable =
        playerSubject.switchMapCompletable { player ->
            if (!player.isPaused()) {
                return@switchMapCompletable Completable.complete()
            }
            player.togglePlayPause()
        }

    private fun pause(): Completable =
        playerSubject.switchMapCompletable { player ->
            player.pause()
        }

    private fun seekTo(seekToPositionMs: Long): Completable =
        playerSubject.switchMapCompletable { player ->
            player.seekTo(positionMs = seekToPositionMs)
        }

    private fun stopPlayback(): Completable =
        playerSubject.switchMapCompletable { player ->
            player.stop()
        }

    private fun selectTrack(trackType: Int, trackGroupIndex: Int, trackIndex: Int) : Completable =
        playerSubject.switchMapCompletable { player ->
            player.selectTrack(trackType = trackType, trackGroupIndex = trackGroupIndex, trackIndex = trackIndex)
        }

    private fun startPlayback(
        manifestUrl: String,
        callbackOnVideoSizeChanged: ((PlayerRatio) -> Unit)?,
        seekToPositionMs: Long?,
        callbackOnPositionStateChanged: ((PositionState) -> Unit)?,
        callbackOnError: ((Throwable) -> Unit),
    ): Completable =
        playerSubject.switchMapCompletable { player ->
            player.setPlayerView(playerView)
                .andThen(
                    player.startPlayback(manifestUrl, callbackOnVideoSizeChanged, seekToPositionMs)
                        .distinct()
                        .doOnNext { newState -> callbackOnPositionStateChanged?.let { it(
                            PositionState(newState.position, newState.durationMs)
                        ) } }
                        .doOnError {
                            callbackOnError(it)
                        }
                        .ignoreElements()
                )
        }

    private fun releasePlayer(blockRestart: Boolean?): Completable =
        playerSubject.flatMapCompletable { player ->
            this.blockRestart = true
            compositeDisposable.dispose()
            player.release()
        }

    private fun initializePlayer(): Single<PlayerCoreImpl> =
        Single.fromCallable {
            PlayerFactory.getPlayerInstance(context)
        }

    private fun Disposable.autoDispose() = compositeDisposable.add(this)

    companion object {
        private const val TAG = "PlayerImpl"
        private const val PLAYER_ACTION_BUFFER_SIZE = 5
    }
}
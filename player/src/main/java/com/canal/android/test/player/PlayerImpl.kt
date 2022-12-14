package com.canal.android.test.player

import android.content.Context
import android.util.Log
import android.view.SurfaceView
import com.canal.android.test.exoplayer.PlayerCoreImpl
import com.canal.android.test.exoplayer.PlayerFactory
import com.canal.android.test.player.model.PlayerAction
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
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

    init {
        observePlayerActions()
        initializePlayer()
            .subscribeOn(Schedulers.io())
            .subscribe({ player ->
                playerSubject.onNext(player)
            }, { throwable ->
                Log.e(TAG, "error", throwable)
            }).autoDispose()
    }

    private fun observePlayerActions() {
        playerActionObservable
            .flatMapCompletable { action ->
                action.handle()
            }
            .subscribe()
            .autoDispose()
    }

    private fun PlayerAction.handle(): Completable =
        when (this) {
            is PlayerAction.StartPlayback -> startPlayback(this.manifestUrl)
            is PlayerAction.Release -> releasePlayer()
            // TODO handle all other actions
            else -> Completable.complete()
        }


    private fun startPlayback(manifestUrl: String): Completable =
        playerSubject.switchMapCompletable { player ->
            player.setPlayerView(playerView)
                .andThen(
                    player.startPlayback(manifestUrl)
                        .ignoreElements()
                )
        }

    private fun releasePlayer(): Completable =
        playerSubject.flatMapCompletable { player ->
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
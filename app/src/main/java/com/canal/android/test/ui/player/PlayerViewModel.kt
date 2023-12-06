package com.canal.android.test.ui.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.canal.android.test.common.PlayerRatio
import com.canal.android.test.common.PositionState
import com.canal.android.test.domain.model.NavigateTo
import com.canal.android.test.domain.usecase.GetMediaUseCase
import com.canal.android.test.ui.common.BaseViewModel
import com.canal.android.test.ui.player.mapper.MediaUiMapper
import com.canal.android.test.ui.player.model.MediaUi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

class PlayerViewModel(
        navigateTo: NavigateTo.QuickTime,
        getMediaUseCase: GetMediaUseCase,
        mediaUiMapper: MediaUiMapper
) : BaseViewModel<MediaUi>() {

    init {
        getMediaUseCase(navigateTo.urlMedias)
                .map { media ->
                    mediaUiMapper.toUi(media)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { mediaUi ->
                            postUiData(mediaUi)
                        },
                        onError = {}
                ).autoDispose()
    }

    private val _playerRatio = MutableLiveData<PlayerRatio>()
    val playerRatio: LiveData<PlayerRatio>
        get() = _playerRatio
    public fun postPlayerRatio(ratio: PlayerRatio) {
        _playerRatio.value = ratio
    }

    private val _playerPositionState = MutableLiveData<PositionState>()
    val playerPositionState: LiveData<PositionState>
        get() = _playerPositionState
    public fun postPlayerPositionState(positionState: PositionState) {
        _playerPositionState.value = positionState
    }
}
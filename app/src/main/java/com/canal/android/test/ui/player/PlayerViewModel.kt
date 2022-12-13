package com.canal.android.test.ui.player

import com.canal.android.test.domain.model.NavigateTo
import com.canal.android.test.domain.usecase.GetMediaUseCase
import com.canal.android.test.ui.common.BaseViewModel
import com.canal.android.test.ui.player.mapper.MediaUiMapper
import com.canal.android.test.ui.player.model.MediaUi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

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
}
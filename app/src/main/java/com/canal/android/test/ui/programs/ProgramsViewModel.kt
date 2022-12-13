package com.canal.android.test.ui.programs

import com.canal.android.test.domain.usecase.GetProgramsUseCase
import com.canal.android.test.ui.common.BaseViewModel
import com.canal.android.test.ui.programs.mapper.ProgramsUiMapper
import com.canal.android.test.ui.programs.model.PageProgramsUi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class ProgramsViewModel(
    getProgramsUseCase: GetProgramsUseCase,
    programsUiMapper: ProgramsUiMapper
) : BaseViewModel<PageProgramsUi>() {

    init {
        getProgramsUseCase()
            .subscribeOn(Schedulers.io())
            .map { programs ->
                programsUiMapper.toUi(programs)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { pageProgramsUi ->
                    postUiData(pageProgramsUi)
                },
                onError = {}
            ).autoDispose()
    }
}
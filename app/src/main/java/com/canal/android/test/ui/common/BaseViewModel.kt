package com.canal.android.test.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel<UI_MODEL> : ViewModel() {

    private val _uiData = MutableLiveData<UI_MODEL>()
    val uiData: LiveData<UI_MODEL>
        get() = _uiData

    private val disposables: CompositeDisposable = CompositeDisposable()

    protected fun postUiData(uiModel: UI_MODEL) {
        _uiData.value = uiModel
    }

    fun Disposable.autoDispose() = disposables.add(this)
    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}
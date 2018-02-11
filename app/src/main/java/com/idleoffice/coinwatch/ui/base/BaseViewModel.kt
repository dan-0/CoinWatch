package com.idleoffice.coinwatch.ui.base

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableBoolean
import com.idleoffice.coinwatch.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable


abstract class BaseViewModel<T>(var schedulerProvider: SchedulerProvider) : ViewModel() {

    var navigator : T? = null

    val compositeDisposable = CompositeDisposable()

    val isLoading = ObservableBoolean(false)


    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
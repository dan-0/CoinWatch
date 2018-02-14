package com.idleoffice.coinwatch.ui.base

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.databinding.ObservableBoolean
import com.idleoffice.coinwatch.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable


abstract class BaseViewModel<T>(app : Application,
                                var schedulerProvider: SchedulerProvider) : AndroidViewModel(app) {

    var navigator : T? = null

    val compositeDisposable = CompositeDisposable()

    val isLoading = ObservableBoolean(false)


    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
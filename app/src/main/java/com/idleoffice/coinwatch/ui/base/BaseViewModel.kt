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

    private var initialized = false

    /**
     * A one time initialization function to help with testing. `init{}` isn't as controllable
     * in unit tests. This requires a view to exist in order to initialize the data
     */
    fun viewInitialize() {
        if(!initialized) {
            initialized = true
            initialize()
        }
    }

    protected open fun initialize() {}

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}
package com.idleoffice.coinwatch.ui.main

import com.idleoffice.coinwatch.rx.SchedulerProvider
import com.idleoffice.coinwatch.ui.base.BaseViewModel
import timber.log.Timber


open class MainViewModel(schedulerProvider: SchedulerProvider) : BaseViewModel<MainNavigator>(schedulerProvider) {

    fun onBtnClickStartDateClick() {
        Timber.e("Start Date Button Clicked")
    }


    fun onBtnClickEndDateClick() {
        Timber.e("End Date Button Clicked")
    }
}
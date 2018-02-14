package com.idleoffice.coinwatch.ui.main

import android.arch.lifecycle.ViewModelProvider
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageInfoService
import com.idleoffice.coinwatch.rx.SchedulerProvider
import com.idleoffice.coinwatch.ui.ViewModelProviderFactory
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {

    @Provides
    fun provideMainViewModel(schedulerProvider: SchedulerProvider, bitcoinAverageInfoService: BitcoinAverageInfoService) : MainViewModel {
        return MainViewModel(schedulerProvider, bitcoinAverageInfoService)
    }

    @Provides
    fun mainViewModelProvider(mainViewModel: MainViewModel) : ViewModelProvider.Factory {
        return ViewModelProviderFactory<Any>(mainViewModel)
    }
}
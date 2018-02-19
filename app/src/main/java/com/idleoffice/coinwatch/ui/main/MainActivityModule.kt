package com.idleoffice.coinwatch.ui.main

import android.app.Application
import android.arch.lifecycle.ViewModelProvider
import com.idleoffice.coinwatch.retrofit.bci.BitcoinAverageInfoService
import com.idleoffice.coinwatch.rx.SchedulerProvider
import com.idleoffice.coinwatch.ui.ViewModelProviderFactory
import dagger.Module
import dagger.Provides

@Module
class MainActivityModule {

    @Provides
    fun provideMainViewModel(
            application: Application,
            schedulerProvider: SchedulerProvider,
            bitcoinAverageInfoService: BitcoinAverageInfoService) : MainViewModel {
        return MainViewModel(application, schedulerProvider, bitcoinAverageInfoService)
    }

    @Provides
    fun mainViewModelProvider(mainViewModel: MainViewModel) : ViewModelProvider.Factory {
        return ViewModelProviderFactory<Any>(mainViewModel)
    }
}
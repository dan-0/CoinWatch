package com.idleoffice.coinwatch.ui.main

import com.idleoffice.coinwatch.MainApp
import com.idleoffice.coinwatch.retrofit.bci.BitcoinAverageInfoService
import com.idleoffice.coinwatch.rx.SchedulerProvider
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test

/**
 * Mostly just a stub test class to assert that we're not experiencing odd underlying issues
 */
class MainActivityModuleTest {

    private var subject = MainActivityModule()

    private var mockApp = mock<MainApp>()
    private var mockSchedulerProvider = mock<SchedulerProvider>()
    private var mockBitcoinAverageInfoService = mock<BitcoinAverageInfoService>()
    private var mockMainViewModel = mock<MainViewModel>()

    @Test
    fun provideMainViewModel() {
        subject.provideMainViewModel(
                mockApp,
                mockSchedulerProvider,
                mockBitcoinAverageInfoService)
    }

    @Test
    fun mainViewModelProvider() {
        subject.mainViewModelProvider(mockMainViewModel)
    }

}
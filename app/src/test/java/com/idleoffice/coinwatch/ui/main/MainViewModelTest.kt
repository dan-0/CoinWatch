package com.idleoffice.coinwatch.ui.main

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.util.Log
import com.idleoffice.coinwatch.MainApp
import com.idleoffice.coinwatch.R
import com.idleoffice.coinwatch.data.model.bci.*
import com.idleoffice.coinwatch.rx.SchedulerProvider
import com.idleoffice.coinwatch.ui.main.graph.CoinLineData
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import timber.log.Timber
import java.util.concurrent.TimeUnit

internal class MainViewModelTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    @Mock
    lateinit var app : MainApp

    private var schedulerProvider = TrampolineSchedulerProvider()

    @Mock
    private
    lateinit var bitcoinAverageInfoService: BitcoinAverageInfoService

    lateinit var subject : MainViewModel

    private val gcHelper = GraphCallHelper()
    private val cpHelper = CurrentPriceHelper()

    val errorMsg = "testErrorMsg"

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        whenever(app.getString(R.string.last_day)).thenReturn("Last Day")
        whenever(app.getString(R.string.all_time)).thenReturn("All Time")
        whenever(app.getString(R.string.last_month)).thenReturn("Last Month")
        whenever(app.getString(R.string.error_from_server)).thenReturn(errorMsg)

        subject = MainViewModel(app, schedulerProvider, bitcoinAverageInfoService)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun doGraphDataCallSuccess() {
        val baiList = gcHelper.baiList
        val sampleName = gcHelper.sampleName
        val stubObservable : Observable<List<BitcoinAverageInfo>> = Observable.just(baiList)

        whenever(bitcoinAverageInfoService.getHistoricalPrice(any(), any()))
                .thenReturn(stubObservable)

        val observer = mock<(CoinLineData) -> Unit>()

        val lifecycle = LifecycleRegistry(mock<LifecycleOwner>())
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        subject.graphData.observe({lifecycle}){
            assert(it!!.equals(CoinLineData(baiList, sampleName)))
            observer.invoke(it)
        }

        subject.doGraphDataCall(gcHelper.symbol, gcHelper.period, sampleName)
        verify(observer).invoke(any<CoinLineData>())
    }

    @Test
    fun doGraphDataCallFail() {
        val sampleName = gcHelper.sampleName
        val stubObservable : Observable<List<BitcoinAverageInfo>> = Observable.error(Exception(errorMsg))
        whenever(bitcoinAverageInfoService.getHistoricalPrice(any(), any()))
                .thenReturn(stubObservable)

        subject.navigator = mock<MainNavigator>()

        subject.doGraphDataCall(gcHelper.symbol, gcHelper.period, sampleName)

        verify(subject.navigator)!!.displayError(errorMsg)
    }

    @Test
    fun doGraphDataCallEnsureDisposed() {
        val stubObservable : Observable<List<BitcoinAverageInfo>> = Observable.just(gcHelper.baiList)
        whenever(bitcoinAverageInfoService.getHistoricalPrice(any(), any()))
                .thenReturn(stubObservable)

        val disposable = mock<Disposable>()

        whenever(disposable.isDisposed).thenReturn(true).thenReturn(false)

        subject.graphDataCall = disposable
        subject.doGraphDataCall(gcHelper.symbol, gcHelper.period, gcHelper.sampleName)
        verify(disposable, times(0)).dispose()
        subject.graphDataCall = disposable
        subject.doGraphDataCall(gcHelper.symbol, gcHelper.period, gcHelper.sampleName)
        verify(disposable, times(1)).dispose()

    }

    @Test
    fun doGetCurrentPriceHappy() {
        val ts = TestScheduler()
        val period = 10L
        setupGetCurrentPrice(ts, period)
        val v1 = cpHelper.cpMap
        val stubObservable : Observable<Map<String, BitcoinAverageCurrent>>
                = Observable.just(v1)
        whenever(bitcoinAverageInfoService.getCurrentPrice(any<String>()))
                .thenReturn(stubObservable)

        val observer = mock<(BitcoinAverageCurrent) -> Unit>()

        val lifecycle = LifecycleRegistry(mock<LifecycleOwner>())
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        var timesCalled = 0
        subject.currentPrice.observe({lifecycle}){
            assert(it!! == cpHelper.cpMap["BTCUSD"])
            observer.invoke(it)
            timesCalled++
        }


        subject.doGetCurrentPrice()
        ts.advanceTimeBy(period * 5L, TimeUnit.SECONDS)
        assertEquals(6, timesCalled)
    }

    @Test
    fun doGetCurrentPriceOnError() {

        val ts = TestScheduler()
        val period = 10L
        setupGetCurrentPrice(ts, period)

        val v1 = cpHelper.cpMap

        val so1 : Observable<Map<String, BitcoinAverageCurrent>>
                = Observable.just(v1)
        val so2 : Observable<Map<String, BitcoinAverageCurrent>>
                = Observable.error(Exception())
        val so3 : Observable<Map<String, BitcoinAverageCurrent>>
                = Observable.error(Exception("Unauthorized"))
        val so4 : Observable<Map<String, BitcoinAverageCurrent>>
                = Observable.error(Exception("errr"))

        whenever(bitcoinAverageInfoService.getCurrentPrice(any<String>()))
                .thenReturn(so1)
                .thenReturn(so2)
                .thenReturn(so3)
                .thenReturn(so4)
                .thenReturn(so1)


        val observer = mock<(BitcoinAverageCurrent) -> Unit>()

        val lifecycle = LifecycleRegistry(mock<LifecycleOwner>())
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        var timesCalled = 0
        subject.currentPrice.observe({lifecycle}){
            assert(it!! == cpHelper.cpMap["BTCUSD"])
            observer.invoke(it)
            timesCalled++
        }

        var nullErrorOccured = false
        var unauthErrorOccured = false
        var otherErrorOccurred = false
        Timber.plant(object : Timber.Tree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                var msg = ""
                if(t!!.message != null) {
                    msg = t.message!!
                }

                if(msg == "") {
                    nullErrorOccured = true
                }
                if(msg.contains("Unauthorized") && priority == Log.WARN) {
                    unauthErrorOccured = true
                }
                if(nullErrorOccured && unauthErrorOccured) {
                    otherErrorOccurred = true
                }
            }
        })

        subject.doGetCurrentPrice()
        ts.advanceTimeBy(period * 5L, TimeUnit.SECONDS)
        assertEquals(3, timesCalled)
        assertEquals(true, nullErrorOccured)
        assertEquals(true, unauthErrorOccured)
        assertEquals(true, otherErrorOccurred)
    }

    @Test
    fun onBtnClickAllTime() {
    }

    @Test
    fun onBtnClickMonth() {
    }

    @Test
    fun onBtnClickDay() {
    }

    @Test
    fun onChartValueSelectedListener() {
    }

    private fun setupGetCurrentPrice(ts : TestScheduler, period : Long) {
        val localScheduler = TestSchedulerProvider(ts)
        subject = spy(MainViewModel(app, localScheduler, bitcoinAverageInfoService))

        val intervalObservable = Observable.interval(0, period, TimeUnit.SECONDS, localScheduler.io())
        whenever(subject.getObservableInterval()).thenReturn(intervalObservable)
    }

    class GraphCallHelper {
        val symbol = SymbolPair.BTC_USD.value
        val period = PeriodUnit.ALL_TIME.value
        val sampleName = "testSampleName"

        val baiList = arrayListOf<BitcoinAverageInfo>(
                BitcoinAverageInfo("2017-01-01", "1234.56"),
                BitcoinAverageInfo("2017-01-02", "1234.567"))
    }

    class CurrentPriceHelper {
        val cpMap = mapOf("BTCUSD" to BitcoinAverageCurrent(last = 1234.11, timestamp = 1518905972))
    }

    class TestSchedulerProvider(var scheduler : TestScheduler) : SchedulerProvider {
        override fun ui(): TestScheduler {return scheduler}
        override fun io(): TestScheduler {return scheduler}
    }

    class TrampolineSchedulerProvider : SchedulerProvider {
        override fun ui(): Scheduler {return Schedulers.trampoline()}
        override fun io(): Scheduler {return Schedulers.trampoline()}
    }
}
package com.idleoffice.coinwatch.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import android.util.Log
import com.github.mikephil.charting.data.Entry
import com.idleoffice.coinwatch.MainApp
import com.idleoffice.coinwatch.R
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageCurrent
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageInfo
import com.idleoffice.coinwatch.data.model.bci.PeriodUnit
import com.idleoffice.coinwatch.data.model.bci.SymbolPair
import com.idleoffice.coinwatch.retrofit.bci.BitcoinAverageInfoService
import com.idleoffice.coinwatch.rx.SchedulerProvider
import com.idleoffice.coinwatch.ui.main.graph.CoinLineData
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import junit.framework.TestCase.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainViewModelTest {

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    @Mock
    private
    lateinit var app : MainApp

    private var schedulerProvider = TrampolineSchedulerProvider()

    @Mock
    private
    lateinit var bitcoinAverageInfoService: BitcoinAverageInfoService

    private lateinit var subject : MainViewModel

    private val gcHelper = GraphCallHelper()
    private val cpHelper = CurrentPriceHelper()

    private val errorMsg = "testErrorMsg"

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
        Timber.uprootAll()
    }

    @Test
    fun testAssertNonFunctional() {
        // Function should be empty, but run through it just in case something gets added that can cause an error
        subject.graphDataCall.dispose()
    }

    @Test
    fun testInitilization() {
        /*
            Called methods are getting tested elsewhere, we're just going to ensure the
            initialization methods keep getting called here.
         */
        subject = spy(MainViewModel(app, schedulerProvider, bitcoinAverageInfoService))

        doNothing().whenever(subject).doGraphDataCall(
                any(),
                any(),
                any())

        doNothing().whenever(subject).doGetCurrentPrice()

        subject.viewInitialize()

        // Calling a second time to ensure that the below are only invoked once still
        subject.viewInitialize()

        verify(subject, times(1)).doGraphDataCall(
                SymbolPair.BTC_USD.value,
                PeriodUnit.DAILY.value,
                app.getString(R.string.last_day))

        verify(subject, times(1)).doGetCurrentPrice()
    }

    @Test
    fun doGraphDataCallHappy() {
        val baiList = gcHelper.baiList
        val sampleName = "doGraphDataCallHappy"
        val stubObservable : Observable<List<BitcoinAverageInfo>> = Observable.just(baiList)

        val mockNavigator = mock<MainNavigator>()
        subject.navigator = mockNavigator

        whenever(bitcoinAverageInfoService.getHistoricalPrice(any(), any()))
                .thenReturn(stubObservable)

        val observer = mock<(CoinLineData) -> Unit>()

        val lifecycle = LifecycleRegistry(mock())
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        subject.graphData.observe({lifecycle}){
            assertEquals(it!!.sampleName, sampleName)
            observer.invoke(it)
        }

        subject.doGraphDataCall(gcHelper.symbol, gcHelper.period, sampleName)
        verify(observer).invoke(any())
        verify(mockNavigator).showLoading()
    }

    @Test
    fun doGraphDataCallFail() {
        val sampleName = gcHelper.sampleName
        val stubObservable : Observable<List<BitcoinAverageInfo>> = Observable.error(Exception(errorMsg))
        whenever(bitcoinAverageInfoService.getHistoricalPrice(any(), any()))
                .thenReturn(stubObservable)

        subject.navigator = mock()

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
        whenever(bitcoinAverageInfoService.getCurrentPrice(any()))
                .thenReturn(stubObservable)

        val observer = mock<(BitcoinAverageCurrent) -> Unit>()

        val lifecycle = LifecycleRegistry(mock())
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

        whenever(bitcoinAverageInfoService.getCurrentPrice(any()))
                .thenReturn(so1)
                .thenReturn(so2)
                .thenReturn(so3)
                .thenReturn(so4)
                .thenReturn(so1)


        val observer = mock<(BitcoinAverageCurrent) -> Unit>()

        val lifecycle = LifecycleRegistry(mock())
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        val onErrorInErrorMsg = "testExecption"
        var timesCalled = 0
        subject.currentPrice.observe({lifecycle}){
            assert(it!! == cpHelper.cpMap["BTCUSD"])
            observer.invoke(it)
            timesCalled++
            if(timesCalled > 2) {
                throw Exception(onErrorInErrorMsg)
            }
        }

        var nullErrorOccured = false
        var unauthErrorOccured = false
        var otherErrorOccurred = false
        var onErrorInErrorResumeNext = false
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

                if(msg == onErrorInErrorMsg) {
                    onErrorInErrorResumeNext = true
                }
            }
        })

        // sanity check
        assertEquals(false, nullErrorOccured)
        assertEquals(false, unauthErrorOccured)
        assertEquals(false, otherErrorOccurred)
        assertEquals(false, onErrorInErrorResumeNext)

        subject.doGetCurrentPrice()
        ts.advanceTimeBy(period * 5L, TimeUnit.SECONDS)
        assertEquals(3, timesCalled)
        assertEquals(true, nullErrorOccured)
        assertEquals(true, unauthErrorOccured)
        assertEquals(true, otherErrorOccurred)
        assertEquals(true, onErrorInErrorResumeNext)
    }

    @Test
    fun onBtnClickAllTime() {
        setupOnBtnClickSpy()
        subject.onBtnClickAllTime()
        verify(subject).doGraphDataCall(
                SymbolPair.BTC_USD.value,
                PeriodUnit.ALL_TIME.value,
                app.getString(R.string.all_time))
    }

    @Test
    fun onBtnClickMonth() {
        setupOnBtnClickSpy()
        subject.onBtnClickMonth()
        verify(subject).doGraphDataCall(
                SymbolPair.BTC_USD.value,
                PeriodUnit.MONTHLY.value,
                app.getString(R.string.last_month))
    }

    @Test
    fun onBtnClickDay() {
        setupOnBtnClickSpy()
        subject.onBtnClickDay()
        verify(subject).doGraphDataCall(
                SymbolPair.BTC_USD.value,
                PeriodUnit.DAILY.value,
                app.getString(R.string.last_day))
    }

    @Test
    fun onChartValueSelectedListenerHappy() {
        val listener = subject.onChartValueSelectedListener()
        subject.graphData.value = CoinLineData(gcHelper.baiList, gcHelper.sampleName)

        subject.navigator = mock()

        val e = Entry(0f, 1234.56f)

        listener.onValueSelected(e, null)
        verify(subject.navigator)!!.xAxisLabel("01 Jan 2017, 01:23")
        verify(subject.navigator)!!.yAxisLabel("$ 1234.56")

        // Function should be empty, but run through it just in case something gets added that can cause an error
        listener.onNothingSelected()
    }

    @Test
    fun onChartValueSelectedListenerNullValues() {
        val listener = subject.onChartValueSelectedListener()

        var nullGraphDataValue = false
        var nullEntry = false

        Timber.plant(object : Timber.Tree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                when(message) {
                    "Graph data was null" -> nullGraphDataValue = true
                    "Entry value was null" -> nullEntry = true
                }
            }
        })

        assertFalse(nullGraphDataValue)
        assertFalse(nullEntry)

        subject.graphData.value = CoinLineData(gcHelper.baiList, gcHelper.sampleName)
        // Assert that we catch a null entry
        listener.onValueSelected(null, null)
        assertFalse(nullGraphDataValue)
        assertTrue(nullEntry)

        subject.graphData.value = null
        listener.onValueSelected(null, null)
        assertTrue(nullGraphDataValue)
        assertTrue(nullEntry)

    }

    private fun setupOnBtnClickSpy() {
        subject = spy(MainViewModel(app, schedulerProvider, bitcoinAverageInfoService))
        doNothing().whenever(subject).doGraphDataCall(any(), any(), any())
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

        val baiList = arrayListOf(
                BitcoinAverageInfo("2017-01-01 01:23:45", "1234.56"),
                BitcoinAverageInfo("2017-01-02 01:23:46", "1234.567"))
    }

    class CurrentPriceHelper {
        val cpMap = mapOf("BTCUSD" to BitcoinAverageCurrent(last = 1234.11, timestamp = 1518905972))
    }

    class TestSchedulerProvider(private var scheduler : TestScheduler) : SchedulerProvider {
        override fun ui(): TestScheduler {return scheduler}
        override fun io(): TestScheduler {return scheduler}
    }

    class TrampolineSchedulerProvider : SchedulerProvider {
        override fun ui(): Scheduler {return Schedulers.trampoline()}
        override fun io(): Scheduler {return Schedulers.trampoline()}
    }
}
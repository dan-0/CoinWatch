package com.idleoffice.coinwatch.ui.main

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.idleoffice.coinwatch.MainApp
import com.idleoffice.coinwatch.R
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageCurrent
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageInfoService
import com.idleoffice.coinwatch.rx.SchedulerProvider
import com.idleoffice.coinwatch.ui.base.BaseViewModel
import com.idleoffice.coinwatch.ui.main.graph.CoinLineData
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


class MainViewModel(
        app : Application,
        schedulerProvider: SchedulerProvider,
        private var bitcoinAverageInfoService: BitcoinAverageInfoService)
    : BaseViewModel<MainNavigator>(app, schedulerProvider) {

    val graphData = MutableLiveData<CoinLineData>()
    val currentPrice = MutableLiveData<BitcoinAverageCurrent>()
    var graphDataCall = object : Disposable {
        override fun isDisposed(): Boolean {return true}
        override fun dispose() {}
    }

    init {
        // initialize to Daily
        doGraphDataCall(
                BitcoinAverageInfoService.Companion.SymbolPair.BTC_USD.value,
                BitcoinAverageInfoService.Companion.PeriodUnit.DAILY.value,
                getApplication<MainApp>().getString(R.string.last_day))

        doGetCurrentPrice()
    }

    private fun doGraphDataCall(symbol : String, period : String, sampleName : String) {
        if(!graphDataCall.isDisposed) {
            Timber.d("Call already in progress, dumping previous call")
            graphDataCall.dispose()
        }

        graphDataCall = bitcoinAverageInfoService.getHistoricalPrice(symbol = symbol, period = period)
                            .subscribeOn(schedulerProvider.io())
                            .observeOn(schedulerProvider.ui())
                            .subscribe(
                                    {n ->
                                        graphData.value = CoinLineData(n.reversed(), sampleName)},
                                    {e ->
                                        Timber.e(e, "Error getting graph data")
                                        val errorMsg = getApplication<MainApp>().getString(R.string.error_from_server)
                                        navigator?.displayError(errorMsg)
                                    })
        compositeDisposable.add(graphDataCall)
    }

    private fun doGetCurrentPrice() {
        val priceGetter = Observable.interval(0,10, TimeUnit.SECONDS)
                .flatMap { bitcoinAverageInfoService.getCurrentPrice(BitcoinAverageInfoService.generateKey())
                        .onErrorResumeNext {t: Throwable ->
                            Timber.e(t, "Error attempting to get current price, trying again.")
                            Observable.empty()} }
                .observeOn(schedulerProvider.ui())
                .subscribe({n -> currentPrice.value = n["BTCUSD"]}, {e -> Timber.e(e)})
        compositeDisposable.add(priceGetter)
    }

    fun onBtnClickAllTime() {
        Timber.d("All time button clicked")
        doGraphDataCall(
                BitcoinAverageInfoService.Companion.SymbolPair.BTC_USD.value,
                BitcoinAverageInfoService.Companion.PeriodUnit.ALL_TIME.value,
                getApplication<MainApp>().getString(R.string.all_time))
    }


    fun onBtnClickMonth() {
        Timber.d("Month button clicked")
        doGraphDataCall(
                BitcoinAverageInfoService.Companion.SymbolPair.BTC_USD.value,
                BitcoinAverageInfoService.Companion.PeriodUnit.MONTHLY.value,
                getApplication<MainApp>().getString(R.string.last_month))
    }

    fun onBtnClickDay() {
        Timber.d("Day button clicked")
        doGraphDataCall(
                BitcoinAverageInfoService.Companion.SymbolPair.BTC_USD.value,
                BitcoinAverageInfoService.Companion.PeriodUnit.DAILY.value,
                getApplication<MainApp>().getString(R.string.last_day))
    }

    fun onChartValueSelectedListener() : OnChartValueSelectedListener {
        return object : OnChartValueSelectedListener {
            override fun onNothingSelected() {}
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                val cf = graphData.value ?: return
                if (e == null) {
                    return
                }
                val time = cf.bcInfo[e.x.roundToInt()].time
                val fromPattern =  "yyyy-MM-dd HH:mm:ss"
                val toPattern = "dd MMM yyy, HH:mm"
                val fromFormatter = SimpleDateFormat(fromPattern, Locale.getDefault())
                val toFormatter = SimpleDateFormat(toPattern, Locale.getDefault())
                val date = fromFormatter.parseObject(time)
                toFormatter.format(date)
                navigator?.xAxisLabel(toFormatter.format(date) as CharSequence)
                navigator?.yAxisLabel(String.format("$ %s", e.y.toString()))
            }
        }
    }
}
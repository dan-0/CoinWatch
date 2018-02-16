package com.idleoffice.coinwatch.ui.main

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.idleoffice.coinwatch.MainApp
import com.idleoffice.coinwatch.R
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageCurrent
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageInfo
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageInfoService
import com.idleoffice.coinwatch.rx.SchedulerProvider
import com.idleoffice.coinwatch.ui.base.BaseViewModel
import com.idleoffice.coinwatch.ui.main.graph.CoinLineData
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    val rxObservers = ArrayList<Disposable>()

    init {
        // initialize to Daily
        val call = bitcoinAverageInfoService.getInfo(
                BitcoinAverageInfoService.Companion.SymbolPair.BTC_USD.value,
                BitcoinAverageInfoService.Companion.PeriodUnit.DAILY.value)

        doGraphDataCall(call, getApplication<MainApp>().getString(R.string.last_day))
        doGetCurrentPrice()
    }

    override fun onCleared() {
        rxObservers
                .filterNot { it.isDisposed }
                .forEach { it.dispose() }
        super.onCleared()
    }

    private fun doGraphDataCall(call : Call<List<BitcoinAverageInfo>>?, sampleName : String) {
        call?.enqueue(
                object : Callback<List<BitcoinAverageInfo>> {
                    override fun onFailure(call: Call<List<BitcoinAverageInfo>>?, t: Throwable?) {
                        Timber.e(t, "Failed call to service")
                        val errorMsg = getApplication<MainApp>().getString(R.string.error_from_server)
                        navigator?.displayError(errorMsg)
                    }

                    override fun onResponse(call: Call<List<BitcoinAverageInfo>>?, response: Response<List<BitcoinAverageInfo>>?) {
                        val body = response?.body()
                        if (body == null) {
                            Timber.e("Got response, but body null")
                            return
                        }
                        graphData.value = CoinLineData(body.reversed(), sampleName)
                    }

                })
    }

    private fun doGetCurrentPrice() {
        val priceGetter = Observable.interval(0,10, TimeUnit.SECONDS)
                .flatMap { bitcoinAverageInfoService.getCurrentPrice(BitcoinAverageInfoService.generateKey())
                        .onErrorResumeNext {t: Throwable ->
                            Timber.e(t, "Error attempting to get current price, trying again.")
                            Observable.empty()} }
                .observeOn(schedulerProvider.ui())
                .subscribe({n -> currentPrice.value = n["BTCUSD"]}, {e -> Timber.e(e)})
        rxObservers.add(priceGetter)
    }

    fun onBtnClickAllTime() {
        Timber.d("All time button clicked")
        val call = bitcoinAverageInfoService.getInfo(
                BitcoinAverageInfoService.Companion.SymbolPair.BTC_USD.value,
                BitcoinAverageInfoService.Companion.PeriodUnit.ALL_TIME.value)
        doGraphDataCall(call, getApplication<MainApp>().getString(R.string.all_time))
    }


    fun onBtnClickMonth() {
        Timber.d("Month button clicked")
        val call = bitcoinAverageInfoService.getInfo(
                BitcoinAverageInfoService.Companion.SymbolPair.BTC_USD.value,
                BitcoinAverageInfoService.Companion.PeriodUnit.MONTHLY.value)
        doGraphDataCall(call, getApplication<MainApp>().getString(R.string.last_month))
    }

    fun onBtnClickDay() {
        Timber.d("Day button clicked")
        val call = bitcoinAverageInfoService.getInfo(
                BitcoinAverageInfoService.Companion.SymbolPair.BTC_USD.value,
                BitcoinAverageInfoService.Companion.PeriodUnit.DAILY.value)
        doGraphDataCall(call, getApplication<MainApp>().getString(R.string.last_day))
    }

    fun onChartValueSelectedListener() : OnChartValueSelectedListener {
        return object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
            }
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
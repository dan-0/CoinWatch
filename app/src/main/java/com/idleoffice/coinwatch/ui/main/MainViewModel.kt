package com.idleoffice.coinwatch.ui.main

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import com.idleoffice.coinwatch.MainApp
import com.idleoffice.coinwatch.R
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageInfo
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageInfoService
import com.idleoffice.coinwatch.rx.SchedulerProvider
import com.idleoffice.coinwatch.ui.base.BaseViewModel
import com.idleoffice.coinwatch.ui.main.graph.CoinLineData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


class MainViewModel(
        app : Application,
        schedulerProvider: SchedulerProvider,
        private var bitcoinAverageInfoService: BitcoinAverageInfoService)
    : BaseViewModel<MainNavigator>(app, schedulerProvider) {

    val graphData = MutableLiveData<CoinLineData>()

    init {
        // initialize to Daily
        val call = bitcoinAverageInfoService.getInfo(
                BitcoinAverageInfoService.Companion.SymbolPair.BTC_USD.value,
                BitcoinAverageInfoService.Companion.PeriodUnit.DAILY.value)

        doGraphDataCall(call)
    }

    private fun doGraphDataCall(call : Call<List<BitcoinAverageInfo>>?) {
        call?.enqueue(
                object : Callback<List<BitcoinAverageInfo>> {
                    override fun onFailure(call: Call<List<BitcoinAverageInfo>>?, t: Throwable?) {
                        Timber.e(t, "Failed call to service")
                        val errorMsg = getApplication<MainApp>().getString(R.string.error_from_server)
                        navigator?.displayError(errorMsg)
                    }

                    override fun onResponse(call: Call<List<BitcoinAverageInfo>>?, response: Response<List<BitcoinAverageInfo>>?) {
                        val body = response?.body()
                        Timber.d("Successful call to service, response: %s", body.toString())
                        if (body == null) {
                            Timber.e("Got response, but body null")
                            return
                        }
                        graphData.value = CoinLineData(body.reversed())
                    }

                })
    }

    fun onBtnClickAllTime() {
        Timber.d("All time button clicked")
        val call = bitcoinAverageInfoService.getInfo(
                BitcoinAverageInfoService.Companion.SymbolPair.BTC_USD.value,
                BitcoinAverageInfoService.Companion.PeriodUnit.ALL_TIME.value)
        doGraphDataCall(call)
    }


    fun onBtnClickMonth() {
        Timber.d("Month button clicked")
        val call = bitcoinAverageInfoService.getInfo(
                BitcoinAverageInfoService.Companion.SymbolPair.BTC_USD.value,
                BitcoinAverageInfoService.Companion.PeriodUnit.MONTHLY.value)
        doGraphDataCall(call)
    }

    fun onBtnClickDay() {
        Timber.d("Day button clicked")
        val call = bitcoinAverageInfoService.getInfo(
                BitcoinAverageInfoService.Companion.SymbolPair.BTC_USD.value,
                BitcoinAverageInfoService.Companion.PeriodUnit.DAILY.value)
        doGraphDataCall(call)
    }
}
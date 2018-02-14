package com.idleoffice.coinwatch.ui.main

import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageInfo
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageInfoService
import com.idleoffice.coinwatch.rx.SchedulerProvider
import com.idleoffice.coinwatch.ui.base.BaseViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber


open class MainViewModel(schedulerProvider: SchedulerProvider, var bitcoinAverageInfoService: BitcoinAverageInfoService)
    : BaseViewModel<MainNavigator>(schedulerProvider) {

    fun onBtnClickAllTime() {
        Timber.d("All time button clicked")
        bitcoinAverageInfoService.getInfo(
                BitcoinAverageInfoService.Companion.SymbolPair.BTC_USD.value,
                BitcoinAverageInfoService.Companion.PeriodUnit.ALL_TIME.value)
                .enqueue(
                        object : Callback<List<BitcoinAverageInfo>> {
                            override fun onFailure(call: Call<List<BitcoinAverageInfo>>?, t: Throwable?) {
                                Timber.e(t, "Failed call to service")
                            }

                            override fun onResponse(call: Call<List<BitcoinAverageInfo>>?, response: Response<List<BitcoinAverageInfo>>?) {
                                val body = response?.body()
                                Timber.d("Successful call to service, response: %s", body.toString())

                            }

                        })
    }


    fun onBtnClickMonth() {
        Timber.d("Month button clicked")
        bitcoinAverageInfoService.getInfo(
                BitcoinAverageInfoService.Companion.SymbolPair.BTC_USD.value,
                BitcoinAverageInfoService.Companion.PeriodUnit.MONTHLY.value)
                .enqueue(
                        object : Callback<List<BitcoinAverageInfo>> {
                            override fun onFailure(call: Call<List<BitcoinAverageInfo>>?, t: Throwable?) {
                                Timber.e(t, "Failed call to service")
                            }

                            override fun onResponse(call: Call<List<BitcoinAverageInfo>>?, response: Response<List<BitcoinAverageInfo>>?) {
                                val body = response?.body()
                                Timber.d("Successful call to service, response: %s", body.toString())

                            }

                        })
    }

    fun onBtnClickDay() {
        Timber.d("Day button clicked")
        val info = bitcoinAverageInfoService.getInfo(
                BitcoinAverageInfoService.Companion.SymbolPair.BTC_USD.value,
                BitcoinAverageInfoService.Companion.PeriodUnit.DAILY.value)
        info.enqueue(
            object : Callback<List<BitcoinAverageInfo>> {
                override fun onFailure(call: Call<List<BitcoinAverageInfo>>?, t: Throwable?) {
                    Timber.e(t, "Failed call to service, data: %s", call.toString())
                }

                override fun onResponse(call: Call<List<BitcoinAverageInfo>>?, response: Response<List<BitcoinAverageInfo>>?) {

                    val body = response?.body()
                    Timber.d("Successful call to service, response: %s", body.toString())

                }

        })

    }
}
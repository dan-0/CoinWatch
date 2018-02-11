package com.idleoffice.coinwatch.rx

import com.idleoffice.coinwatch.data.model.coindata.CoinData
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class AppSchedulerProvider : SchedulerProvider {
    override fun ui(): Scheduler {
        val cd : CoinData
        return AndroidSchedulers.mainThread()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }
}
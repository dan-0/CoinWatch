package com.idleoffice.coinwatch

import android.app.Activity
import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.idleoffice.coinwatch.Const.COIN_WATCH_PREFS
import com.idleoffice.coinwatch.Const.HISTORICAL_COIN_DB_NAME
import com.idleoffice.coinwatch.Const.PREF_INIT
import com.idleoffice.coinwatch.dagger.DaggerAppComponent
import com.idleoffice.coinwatch.data.db.AppDatabase
import com.idleoffice.coinwatch.data.model.coindata.CoinData
import com.idleoffice.coinwatch.data.model.coindata.historical.HistoricalCoinData
import com.squareup.moshi.*
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import okio.Buffer
import okio.ForwardingSource
import okio.Okio
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class MainApp : Application(), HasActivityInjector {

    @Inject
    lateinit var activityDispatchingAndroidInjector : DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            throw NotImplementedError("Release debugging not implemented")
        }

        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)

        val prefs = getSharedPreferences(COIN_WATCH_PREFS, Context.MODE_PRIVATE)
        if(!prefs.getBoolean(PREF_INIT, false)) {
            try {
                loadHistoricalData()
                prefs.edit().putBoolean(PREF_INIT, true).apply()
            } catch (e : LoadHistoricDataException) {
                Timber.e(e, "Error loading historical data: %s", e.message)
            }
        }
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityDispatchingAndroidInjector
    }

    @Throws(LoadHistoricDataException::class)
    private fun loadHistoricalData() {

        val coinData = getJsonAdapter()

        val db = Room.databaseBuilder(
                this, AppDatabase::class.java, HISTORICAL_COIN_DB_NAME).build()
        try {
            val coinDataDao = db.coinDataDao()

            coinData.asIterable().toObservable()
                    .subscribeOn(Schedulers.io())
                    .subscribeBy(
                            onNext = {
                                it.value.amount = it.value.amount.replace(".", "")
                                val amount = it.value.amount.toLong()

                                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val date = sdf.parse(it.key).time
                                val cd = CoinData(it.value.currency, it.value.base, amount, date)
                                coinDataDao.insertCoinData(cd)
                            },
                            onError = {
                                Timber.e(it, "Error processing historical data")
                            },
                            onComplete = {
                                Timber.d("All historical data done processing")
                            }
                    )
        } finally {
           db.close()
        }

    }

    @Throws(LoadHistoricDataException::class)
    private fun getJsonAdapter() : Map<String, HistoricalCoinData> {
        assets.open("historicalBtcData.txt").use {
            val historicalDataType = Types.newParameterizedType(
                    Map::class.java, String::class.java, HistoricalCoinData::class.java)

            val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()

            val adapter: JsonAdapter<Map<String, HistoricalCoinData>> = moshi.adapter(historicalDataType)
            val buffer = Buffer().readFrom(it)
            val src = object : ForwardingSource(buffer) {}
            val jr = JsonReader.of(Okio.buffer(src))
            jr.isLenient = true

            return adapter.fromJson(jr) ?: throw LoadHistoricDataException("No json data returned")
        }
    }

    class LoadHistoricDataException(override val message : String) : Exception(message)
}
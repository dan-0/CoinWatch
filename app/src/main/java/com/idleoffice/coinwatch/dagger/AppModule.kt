package com.idleoffice.coinwatch.dagger

import com.idleoffice.coinwatch.retrofit.bci.BitcoinAverageInfoService
import com.idleoffice.coinwatch.rx.AppSchedulerProvider
import com.idleoffice.coinwatch.rx.SchedulerProvider
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    fun provideSchedulerProvider() : SchedulerProvider {
        return AppSchedulerProvider()
    }

    @Provides
    @Singleton
    fun provideBitcoinAverageInfoService() : BitcoinAverageInfoService {
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        return Retrofit.Builder()
                .baseUrl(BitcoinAverageInfoService.BITCOIN_INFO_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(BitcoinAverageInfoService::class.java)
    }
}
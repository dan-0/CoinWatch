package com.idleoffice.coinwatch.dagger

import android.app.Application
import android.content.Context
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageInfoService
import com.idleoffice.coinwatch.rx.AppSchedulerProvider
import com.idleoffice.coinwatch.rx.SchedulerProvider
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(application : Application) : Context {
        return application
    }

    @Provides
    fun provideSchedulerProvider() : SchedulerProvider {
        return AppSchedulerProvider()
    }

    @Provides
    @Singleton
    fun provideBitcoinAverageInfoService() : BitcoinAverageInfoService {
//        val listBcInfoType = Types.newParameterizedType(List::class.java, BitcoinAverageInfo::class.java)
//        val moshi = Moshi.Builder().build()
//        val adapter = moshi.adapter<List<BitcoinAverageInfo>>(listBcInfoType)
//        moshi.ad
        val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
//        moshi.adapter<List<BitcoinAverageInfo>>(listBcInfoType)
//        moshi.adapter<List<BitcoinAverageInfo>>(listBcInfoType)
        return Retrofit.Builder()
                .baseUrl(BitcoinAverageInfoService.BITCOIN_INFO_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                .build()
                .create(BitcoinAverageInfoService::class.java)
    }
}
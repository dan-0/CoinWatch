package com.idleoffice.coinwatch.dagger

import android.app.Application
import android.content.Context
import com.idleoffice.coinwatch.rx.AppSchedulerProvider
import com.idleoffice.coinwatch.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
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
}
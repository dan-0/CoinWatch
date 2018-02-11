package com.idleoffice.coinwatch.dagger

import android.app.Application
import com.idleoffice.coinwatch.MainApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AppModule::class, ActivityBuilder::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application : Application) : Builder

        fun build() : AppComponent
    }

    fun inject(app : MainApp)
}
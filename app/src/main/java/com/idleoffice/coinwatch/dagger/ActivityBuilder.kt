package com.idleoffice.coinwatch.dagger

import com.idleoffice.coinwatch.ui.main.MainActivity
import com.idleoffice.coinwatch.ui.main.MainActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindMainActivity() : MainActivity
}
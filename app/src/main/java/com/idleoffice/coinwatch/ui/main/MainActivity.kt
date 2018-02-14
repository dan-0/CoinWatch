package com.idleoffice.coinwatch.ui.main

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.SeekBar
import com.idleoffice.coinwatch.BR
import com.idleoffice.coinwatch.R
import com.idleoffice.coinwatch.databinding.ActivityMainBinding
import com.idleoffice.coinwatch.ui.base.BaseActivity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), MainNavigator, HasSupportFragmentInjector {
    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var activityMainBinding : ActivityMainBinding? = null

    @Inject
    lateinit var mainViewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("Main activity onCreate called")
        super.onCreate(savedInstanceState)
        this.activityMainBinding = viewDataBinding
        mainViewModel.navigator = this
    }

    override fun getActivityViewModel(): MainViewModel {
        return mainViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun setSeekerMax(max: Int) {
        val seekBar = findViewById<SeekBar>(R.id.seekBar)
        seekBar.max = max
    }

    override fun handleError(t: Throwable) {
        Timber.e(t)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return fragmentDispatchingAndroidInjector
    }
}

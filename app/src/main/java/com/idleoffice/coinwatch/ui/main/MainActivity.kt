package com.idleoffice.coinwatch.ui.main

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.SeekBar
import android.widget.Toast
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.idleoffice.coinwatch.BR
import com.idleoffice.coinwatch.R
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageInfo
import com.idleoffice.coinwatch.databinding.ActivityMainBinding
import com.idleoffice.coinwatch.ui.base.BaseActivity
import com.idleoffice.coinwatch.ui.main.graph.CoinLineData
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), MainNavigator, HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    var activityMainBinding : ActivityMainBinding? = null

    @Inject
    lateinit var mainViewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("Main activity onCreate called")
        super.onCreate(savedInstanceState)
        this.activityMainBinding = viewDataBinding
        mainViewModel.navigator = this

        val graphDataObserver = Observer<CoinLineData> {
            if (it == null) {
                return@Observer
            }
            Timber.d("Detected graph data change")
            updateGraph(it.getLineData())
            updateSeeker(it.bcInfo)
        }

        mainViewModel.graphData.observe(this, graphDataObserver)
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

    override fun displayError(errorMsg : String) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment>? {
        return fragmentDispatchingAndroidInjector
    }

    private fun updateGraph(lineData: LineData) {
        val chart = findViewById<LineChart>(R.id.chart)
        chart.data = lineData

        chart.invalidate()
    }

    private fun updateSeeker(coinInfo : List<BitcoinAverageInfo>) {
        val seeker = findViewById<SeekBar>(R.id.seekBar)
        seeker.max = coinInfo.size
    }

    override fun updatePrice(price: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

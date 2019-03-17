package com.idleoffice.coinwatch.ui.main

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.github.mikephil.charting.components.Description
import com.idleoffice.coinwatch.BR
import com.idleoffice.coinwatch.R
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageCurrent
import com.idleoffice.coinwatch.databinding.ActivityMainBinding
import com.idleoffice.coinwatch.ui.base.BaseActivity
import com.idleoffice.coinwatch.ui.main.graph.CoinLineData
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.progress_bar_frame_layout.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(), MainNavigator, HasSupportFragmentInjector {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private var activityMainBinding : ActivityMainBinding? = null

    var mainViewModel : MainViewModel? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("Main activity onCreate called")
        super.onCreate(savedInstanceState)
        this.activityMainBinding = viewDataBinding
        mainViewModel?.navigator = this
        setGraphObserver()
        setPriceObserver()
        initChart()
    }

    private fun setGraphObserver() {
        val graphDataObserver = Observer<CoinLineData> @Synchronized {
            if (it != null) {
                Timber.d("Detected graph data change")
                updateChart(it)
            }
            hideLoading()
        }

        mainViewModel?.graphData?.observe(this, graphDataObserver)
    }

    private fun setPriceObserver() {
        val priceDataObserver = Observer<BitcoinAverageCurrent> @Synchronized {
            if (it != null) {
                Timber.d("Detected graph data change")
                updatePrice(String.format("%.2f", it.last))
            }
        }

        mainViewModel?.currentPrice?.observe(this, priceDataObserver)
    }

    override fun getProgressBar(): ProgressBar? {
        return progressBar
    }

    override fun getActivityViewModel(): MainViewModel {
        if(mainViewModel == null) {
            mainViewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        }

        return mainViewModel as MainViewModel
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
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

    private fun initChart() {
        chart.setOnChartValueSelectedListener(mainViewModel?.onChartValueSelectedListener())
        chart.axisLeft.textColor = ContextCompat.getColor(this, R.color.textPrimary)
    }

    private fun updateChart(data : CoinLineData) {
        val lineData = data.getLineData()
        xAxisLabel("")
        yAxisLabel("")
        chart.legend.textColor = ContextCompat.getColor(this, R.color.textPrimary)
        chart.xAxis.setDrawLabels(false)
        chart.axisRight.setDrawLabels(false)
        chart.data = lineData
        val description = Description()
        description.textColor = ContextCompat.getColor(this, R.color.textPrimary)
        description.text = data.sampleName
        chart.description = description

        chart.invalidate()
    }

    override fun xAxisLabel(date: CharSequence) {
        xAxisLabel.text = date
    }

    override fun yAxisLabel(value: CharSequence) {
        yAxisLabel.text = value
    }

    override fun updatePrice(price: String) {
        priceText.text = price
    }
}

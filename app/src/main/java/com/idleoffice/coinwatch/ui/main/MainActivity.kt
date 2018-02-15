package com.idleoffice.coinwatch.ui.main

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.github.mikephil.charting.charts.LineChart
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
        setGraphObserver()
        setPriceObserver()
        initChart()

    }

    private fun setGraphObserver() {
        val graphDataObserver = Observer<CoinLineData> {
            if (it == null) {
                return@Observer
            }
            Timber.d("Detected graph data change")
            updateChart(it)
        }

        mainViewModel.graphData.observe(this, graphDataObserver)
    }

    private fun setPriceObserver() {
        val priceDataObserver = Observer<BitcoinAverageCurrent> {
            if (it == null) {
                return@Observer
            }
            Timber.d("Detected graph data change")
            updatePrice(String.format("%.2f", it.last))
        }

        mainViewModel.currentPrice.observe(this, priceDataObserver)
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
        val chart = findViewById<LineChart>(R.id.chart)
        chart.setOnChartValueSelectedListener(mainViewModel.onChartValueSelectedListener())
        chart.axisLeft.textColor = ContextCompat.getColor(this, R.color.textPrimary)
    }

    private fun updateChart(data : CoinLineData) {
        val lineData = data.getLineData()
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

    override fun xAxisLabel(label : CharSequence) {
        xAxisLabel.text = label
    }

    override fun valueLabel(label: CharSequence) {
        yAxisLabel.text = label
    }

    override fun updatePrice(price: String) {
        priceText.text = price
    }
}

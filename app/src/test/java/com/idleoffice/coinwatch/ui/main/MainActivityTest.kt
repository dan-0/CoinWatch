package com.idleoffice.coinwatch.ui.main

import android.view.View
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.idleoffice.coinwatch.BR
import com.idleoffice.coinwatch.R
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageCurrent
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageInfo
import com.idleoffice.coinwatch.ui.main.graph.CoinLineData
import io.fabric.sdk.android.Fabric
import junit.framework.TestCase.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.progress_bar_frame_layout.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
class MainActivityTest {

    private lateinit var subject: MainActivity

    @Before
    fun setUp() {
        subject = Robolectric.setupActivity(MainActivity::class.java)

        // Must disable crashlytics for testing
        val core = CrashlyticsCore.Builder().disabled(true).build()
        Fabric.with(subject, Crashlytics.Builder().core(core).build())
    }

    @Test
    fun onCreate() {
        subject = Robolectric.buildActivity(MainActivity::class.java).create().get()

        assertNotNull(subject.viewDataBinding)
        assertEquals(subject, subject.mainViewModel!!.navigator)

        assertTrue(subject.mainViewModel!!.graphData.hasObservers())
        assertTrue(subject.mainViewModel!!.currentPrice.hasObservers())

        assertNotNull(subject.viewModelFactory)
    }

    @Test
    fun testPriceObserver() {
        val currentPrice = subject.mainViewModel!!.currentPrice

        //"Feb 2018, 23:11"
        currentPrice.value = BitcoinAverageCurrent(123.12, 151892711)

        assertEquals(subject.priceText.text, "123.12")

        // Shouldn't change
        currentPrice.value = null
        assertEquals(subject.priceText.text, "123.12")
    }

    @Test
    fun testGraphObserver() {
        val graphData = subject.mainViewModel!!.graphData
        val testDataArray = arrayListOf(
                BitcoinAverageInfo("2017-01-01 01:23:45", "1234.56"),
                BitcoinAverageInfo("2017-01-02 01:23:46", "1234.567"))
        val testSampleName = "testSample"

        val cld = CoinLineData(testDataArray, testSampleName)

        subject.showLoading()
        // Assert we are showing a loading progressbar
        assertEquals(View.VISIBLE, subject.progressBar.visibility)
        graphData.value = cld

        assertEquals(testSampleName, subject.chart.description.text)

        // Check by index for granularity
        assertEquals(subject.chart.data.dataSets[0].getEntryForIndex(0).x,
                cld.getLineData().dataSets[0].getEntryForIndex(0).x)
        assertEquals(
                subject.chart.data.dataSets[0].getEntryForIndex(0).y,
                cld.getLineData().dataSets[0].getEntryForIndex(0).y)
        assertEquals(subject.chart.data.dataSets[0].getEntryForIndex(1).x,
                cld.getLineData().dataSets[0].getEntryForIndex(1).x)
        assertEquals(
                subject.chart.data.dataSets[0].getEntryForIndex(1).y,
                cld.getLineData().dataSets[0].getEntryForIndex(1).y)

        // Should retain through null assignment
        graphData.value = null
        assertEquals(testSampleName, subject.chart.description.text)
        assertEquals(subject.chart.data.dataSets[0].getEntryForIndex(0).x,
                cld.getLineData().dataSets[0].getEntryForIndex(0).x)
        assertEquals(
                subject.chart.data.dataSets[0].getEntryForIndex(0).y,
                cld.getLineData().dataSets[0].getEntryForIndex(0).y)
        assertEquals(subject.chart.data.dataSets[0].getEntryForIndex(1).x,
                cld.getLineData().dataSets[0].getEntryForIndex(1).x)
        assertEquals(
                subject.chart.data.dataSets[0].getEntryForIndex(1).y,
                cld.getLineData().dataSets[0].getEntryForIndex(1).y)

        // Assert progress bar is gone
        assertEquals(View.GONE, subject.progressBar.visibility)
    }

    @Test
    fun getActivityViewModel() {
        val viewModel = subject.getActivityViewModel()
        assertNotNull(viewModel)
        subject.recreate()
        assertNotNull(viewModel)
    }

    @Test
    fun getBindingVariable() {
        assertEquals(BR.viewModel, subject.getBindingVariable())
    }

    @Test
    fun getLayoutId() {
        assertEquals(R.layout.activity_main, subject.getLayoutId())
    }

    @Test
    fun handleError() {
        //Pretty much a stub
        subject.handleError(Exception())
    }

    @Test
    fun displayError() {
        val msg = "Toast Message"
        subject.displayError(msg)
        assertEquals(msg, ShadowToast.getTextOfLatestToast())
    }

    @Test
    fun supportFragmentInjector() {
        assertNotNull(subject.supportFragmentInjector())
    }

    @Test
    fun xAxisLabel() {
        val date = "01 Jan 2017, 01:23"
        subject.xAxisLabel(date)
        assertEquals(date, subject.xAxisLabel.text)
    }

    @Test
    fun yAxisLabel() {
        val price = "0.00"
        subject.yAxisLabel(price)
        assertEquals(price, subject.yAxisLabel.text)
    }

    @Test
    fun updatePrice() {
        val price = "0.00"
        subject.updatePrice(price)
        assertEquals(price, subject.priceText.text)
    }

}
package com.idleoffice.coinwatch.ui.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.RelativeLayout.CENTER_IN_PARENT
import dagger.android.AndroidInjection
import timber.log.Timber


abstract class BaseActivity <T : ViewDataBinding, V : BaseViewModel<*>> : AppCompatActivity() {

    private var progressBar : ProgressBar? = null
    var viewDataBinding : T? = null
    var viewModel : V? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        performDependencyInjection()
        Timber.d("Base activity onCreate called")
        super.onCreate(savedInstanceState)
        if(viewModel == null) {
            viewModel = getActivityViewModel()
        }
        initDataBinding()
    }

    private fun initDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, getLayoutId())
        viewDataBinding?.setVariable(getBindingVariable(), viewModel)
        viewDataBinding?.executePendingBindings()
    }

    fun showLoading() {
        if(progressBar == null) {
            progressBar = ProgressBar(this, null, android.R.attr.progressBarStyle)
            val params = RelativeLayout.LayoutParams(100, 100)
            params.addRule(CENTER_IN_PARENT)
            addContentView(progressBar, params)
        }

        window.setFlags(FLAG_NOT_TOUCHABLE, FLAG_NOT_TOUCHABLE)
        progressBar?.visibility = View.VISIBLE
    }

    fun hideLoading() {
        progressBar?.visibility = View.GONE

        window.clearFlags(FLAG_NOT_TOUCHABLE)
    }

    /**
     * @return
     *      The view model
     */
    abstract fun getActivityViewModel() : V

    /**
     * @return
     *      The variable ID
     */
    abstract fun getBindingVariable() : Int

    /**
     * @return
     *      The Layout ID
     */
    @LayoutRes
    abstract fun getLayoutId() : Int

    fun performDependencyInjection() {
        AndroidInjection.inject(this)
    }
}
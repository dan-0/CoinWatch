package com.idleoffice.coinwatch.ui.base

import android.arch.lifecycle.ViewModel
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.os.PersistableBundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.RelativeLayout.CENTER_IN_PARENT
import dagger.android.AndroidInjection


abstract class BaseActivity <T : ViewDataBinding, V : BaseViewModel<*>> : AppCompatActivity() {

    private var progressBar : ProgressBar? = null
    var viewDataBinding : T? = null
        private set
    var viewModel : V? = null
        private set


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        doInjection()
        super.onCreate(savedInstanceState, persistentState)
        viewModel = getActivityViewModel()
        viewDataBinding = initDataBinding(viewModel!!)
    }


    private fun initDataBinding(viewModel: ViewModel) : T {
        val dataBinding : T = DataBindingUtil.setContentView(this, getLayoutId())
        dataBinding.setVariable(getBindingVariable(), viewModel)
        dataBinding.executePendingBindings()
        return dataBinding
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
     * Inject this
     */
    private fun doInjection() {
        AndroidInjection.inject(this)
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
}
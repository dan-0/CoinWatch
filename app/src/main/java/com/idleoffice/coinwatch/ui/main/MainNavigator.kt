package com.idleoffice.coinwatch.ui.main


interface MainNavigator {
    fun handleError(t : Throwable)
    fun displayError(errorMsg : String)
    fun updatePrice(price : String)
    fun xAxisLabel(date : CharSequence)
    fun yAxisLabel(value : CharSequence)
    fun showLoading()
    fun hideLoading()
}
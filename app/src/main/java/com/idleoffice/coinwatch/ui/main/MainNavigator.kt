package com.idleoffice.coinwatch.ui.main


interface MainNavigator {

    fun setSeekerMax(max : Int)
    fun handleError(t : Throwable)
    fun displayError(errorMsg : String)
    fun updatePrice(price : String)
}
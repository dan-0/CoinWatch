package com.idleoffice.coinwatch.ui

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider


class ViewModelProviderFactory<out V>(private val viewModel : V) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(viewModel as Class<*>)) {
            return viewModel as T
        }

        throw IllegalArgumentException("Classname not known")
    }
}


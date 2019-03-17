package com.idleoffice.coinwatch.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class ViewModelProviderFactory<V : Any>(private var viewModel : V) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(viewModel::class.java)) {
            return viewModel as T
        }


        throw IllegalArgumentException("Classname not known")
    }
}


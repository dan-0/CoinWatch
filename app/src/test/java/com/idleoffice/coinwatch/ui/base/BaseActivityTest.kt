package com.idleoffice.coinwatch.ui.base

import com.idleoffice.coinwatch.databinding.ActivityMainBinding
import com.idleoffice.coinwatch.ui.main.MainViewModel
import com.nhaarman.mockito_kotlin.mock
import junit.framework.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * This is just here until we have enough real activities to test all functionality
 */
class BaseActivityTest {

    lateinit var subject : BaseActivity<ActivityMainBinding, MainViewModel>

    @Before
    fun setup() {
        subject = object : BaseActivity<ActivityMainBinding, MainViewModel>() {
            override fun getActivityViewModel(): MainViewModel {return mock()}
            override fun getBindingVariable(): Int {return mock()}
            override fun getLayoutId(): Int {return mock()}
        }
    }

    @Test
    fun stubTestNullValue() {
        assertNull(subject.viewModel)
    }

    @Test
    fun getProgressBarFrame() {
        assertNull(subject.getProgressBarFrame())
    }

}
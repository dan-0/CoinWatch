package com.idleoffice.coinwatch.ui.base

import com.idleoffice.coinwatch.ui.main.MainNavigator
import com.nhaarman.mockito_kotlin.mock
import io.reactivex.disposables.Disposable
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test

/**
 * This is just here until we have enough real ViewModels to test all functionality
 */
class BaseViewModelTest {

    private var subject = HelperViewModel()

    @Test
    fun onCleared() {
        val d = object : Disposable {
            var disposed = false
            override fun isDisposed(): Boolean {
                return disposed
            }

            override fun dispose() {
                disposed = true
            }

        }

        subject.compositeDisposable.add(d)

        assertFalse(d.isDisposed)
        subject.clearTest()
        assertTrue(d.isDisposed)

    }

    @Test
    fun testStubInit() {
        assertFalse(subject.inited)
        subject.viewInitialize()
        assertTrue(subject.inited)

    }

    private class HelperViewModel : BaseViewModel<MainNavigator>(mock(), mock()) {

        var inited = false

        override fun initialize() {
            inited = true
            super.initialize()
        }

        fun clearTest() {
            super.onCleared()
        }
    }

}
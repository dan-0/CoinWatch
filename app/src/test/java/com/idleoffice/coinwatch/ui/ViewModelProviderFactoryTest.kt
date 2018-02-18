package com.idleoffice.coinwatch.ui

import com.idleoffice.coinwatch.ui.base.BaseViewModel
import com.idleoffice.coinwatch.ui.main.MainNavigator
import com.nhaarman.mockito_kotlin.mock
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test

class ViewModelProviderFactoryTest {

    private lateinit var subject : ViewModelProviderFactory<HelperViewModel>

    @Before
    fun setup() {
        subject = ViewModelProviderFactory(HelperViewModel())
    }

    @Test
    fun create() {
        var badClass = false

        // Good
        assertNotNull(subject.create(HelperViewModel::class.java))

        // Bad
        try {
            subject.create(BadViewModel::class.java)
        } catch (e : IllegalArgumentException) {
            badClass = true
        }

        assertTrue(badClass)

    }

    private class HelperViewModel : BaseViewModel<MainNavigator>(mock(), mock())
    private class BadViewModel : BaseViewModel<MainNavigator>(mock(), mock())

}
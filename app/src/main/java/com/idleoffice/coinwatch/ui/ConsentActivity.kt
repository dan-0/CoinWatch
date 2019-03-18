package com.idleoffice.coinwatch.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import com.idleoffice.coinwatch.R
import com.idleoffice.coinwatch.ui.main.MainActivity
import com.idleoffice.idleconsent.IdleConsent
import com.idleoffice.idleconsent.IdleConsentCallback
import com.idleoffice.idleconsent.IdleConsentConfig
import com.idleoffice.idleconsent.IdleInfoSource

class ConsentActivity : AppCompatActivity() {
    private val idleConfig = IdleConsentConfig(
            "Terms and Conditions",
            "Please take a brief moment to read through our terms and conditions.",
            "To ensure the best experience, we collect anonymized user data to inform us of crashes and how our users interact with the app.",
            listOf("Device information", "Usage statistics", "Advertising ID"),
            IdleInfoSource(
                    "Please see our full privacy policy.",
                    Uri.parse("https://dan-0.gitlab.io/coinwatch-web/privacy-policy")
            ),
            true,
            "Please take a moment and read our privacy policy",
            true,
            "In order to use Super Testy App we require that you agree to our terms and conditions:",
            IdleInfoSource("See full terms and conditions", Uri.parse("https://dan-0.gitlab.io/coinwatch-web/terms"))
    )

    private val consentCallback = object: IdleConsentCallback() {
        override fun onAccept(hasUserAgreedToTerms: Boolean, hasUserAgreedToPrivacy: Boolean) {
            if (!hasUserAgreedToTerms) {
                return
            }

            startMainActivity()
        }
    }

    private fun startMainActivity() {
        if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
            return
        }
        Intent(this@ConsentActivity, MainActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.consent)
        val consent = IdleConsent.getInstance(this)
        if (!consent.hasUserAgreedToTerms) {
            consent.showConsentDialog(supportFragmentManager, consentCallback, idleConfig)
        } else {
            startMainActivity()
        }
    }
}
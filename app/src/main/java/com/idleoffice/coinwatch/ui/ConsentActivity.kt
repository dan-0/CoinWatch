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
            consentTitle = "Disclosures",
            introStatement = "Please take a moment to read through our privacy disclosure and terms of service.",
            dataCollectedSummary = "To ensure the best experience, we collect the following anonymized user data to inform us of crashes and how our users interact with the app:",
            dataCollected = listOf("Device information", "Usage statistics", "Advertising ID"),
            privacyInfoSource = IdleInfoSource.Web(
                    "Please see our full privacy policy.",
                    Uri.parse("https://dan-0.gitlab.io/coinwatch-web/privacy-policy")
            ),
            requirePrivacy = true,
            acceptPrivacyPrompt = "Please take a moment and read our privacy policy",
            privacyPromptChecked = true,
            termsSummary = "Please take the time to look at our terms and conditions:",
            termsInfoSource = IdleInfoSource.Web("See full terms and conditions", Uri.parse("https://dan-0.gitlab.io/coinwatch-web/terms"))
    )

    private val consentCallback = object: IdleConsentCallback() {
        override fun onAcknowledged(hasUserAgreedToTerms: Boolean, hasUserAgreedToPrivacy: Boolean) {
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
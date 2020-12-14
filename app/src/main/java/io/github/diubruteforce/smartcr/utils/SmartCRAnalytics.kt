package io.github.diubruteforce.smartcr.utils

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

object SmartCRAnalytics {
    private val firebaseAnalytics by lazy { Firebase.analytics }

    fun setScreenView(screen: String) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screen)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screen)
        }

        SmartCRCrashlytics.setScreenView(screen)
    }
}
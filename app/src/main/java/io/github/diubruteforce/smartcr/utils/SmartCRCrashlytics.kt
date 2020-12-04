package io.github.diubruteforce.smartcr.utils

import com.google.firebase.crashlytics.FirebaseCrashlytics
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

object SmartCRCrashlytics {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    fun setUserId(id: String) {
        crashlytics.setUserId(id)
    }

}

object SmartCRCrashlyticsTree : Timber.Tree() {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t != null) {
            crashlytics.recordException(t)
        }
    }
}

object SmartCRCrashlyticsOkHttpLogger : HttpLoggingInterceptor.Logger {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    override fun log(message: String) {
        crashlytics.log(message)
    }
}
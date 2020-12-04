package io.github.diubruteforce.smartcr

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.diubruteforce.smartcr.utils.SmartCRCrashlyticsTree
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class SmartCRApp : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.uprootAll()
            Timber.plant(Timber.DebugTree())
            Timber.plant(SmartCRCrashlyticsTree)
        } else{
            Timber.uprootAll()
            Timber.plant(SmartCRCrashlyticsTree)
        }
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
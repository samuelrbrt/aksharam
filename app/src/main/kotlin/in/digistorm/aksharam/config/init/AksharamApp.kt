package `in`.digistorm.aksharam.config.init

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AksharamApp : Application() {

    override fun onCreate() {
        super.onCreate()

        System.setProperty("kotlinx.coroutines.debug", "on")
        Timber.plant(Timber.DebugTree())
    }
}
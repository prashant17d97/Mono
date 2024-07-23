package com.debugdesk.mono.di

import android.app.Application
import com.debugdesk.mono.di.Modules.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/*, Configuration.Provider */
/*override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
        .setWorkerFactory(KoinWorkerFactory())
        .build()*/

class MonoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MonoApplication)
            modules(appModule)
        }
    }
}

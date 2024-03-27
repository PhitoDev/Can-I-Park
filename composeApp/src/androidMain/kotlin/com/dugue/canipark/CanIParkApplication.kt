package com.dugue.canipark

import android.app.Application
import com.dugue.canipark.di.modules.appModule
import di.modules.androidModules
import di.modules.sharedModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CanIParkApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CanIParkApplication)
            modules(androidModules, sharedModules, appModule)
        }
    }
}
package com.ralphdugue.canipark

import android.app.Application
import com.ralphdugue.canipark.di.modules.appModule
import di.modules.androidModules
import di.modules.sharedModules
import org.koin.core.context.startKoin

class CanIParkApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(androidModules, sharedModules, appModule)
        }
    }
}
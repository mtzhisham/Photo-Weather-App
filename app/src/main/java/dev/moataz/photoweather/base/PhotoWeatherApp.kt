package dev.moataz.photoweather.base

import android.app.Application
import dev.moataz.photoweather.di.getModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PhotoWeatherApp() : Application() {

    override fun onCreate() {
        super.onCreate()


        startKoin {
            this@PhotoWeatherApp
            androidContext(this@PhotoWeatherApp)
            modules(*getModules())
        }

    }

}
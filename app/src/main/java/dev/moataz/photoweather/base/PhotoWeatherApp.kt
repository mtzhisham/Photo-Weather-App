package dev.moataz.photoweather.base

import android.app.Application
import dev.moataz.photoweather.BuildConfig
import dev.moataz.photoweather.di.getModules
import dev.moataz.photoweather.helper.Constant.BASE_URL
import dev.moataz.photoweather.network.Network
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PhotoWeatherApp() : Application() {

    override fun onCreate() {
        super.onCreate()

        Network.init(BASE_URL, BuildConfig.DEBUG)

        startKoin {
            this@PhotoWeatherApp
            androidContext(this@PhotoWeatherApp)
            modules(*getModules())
        }

    }

}
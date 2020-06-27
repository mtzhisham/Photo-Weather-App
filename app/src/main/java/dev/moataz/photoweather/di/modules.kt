package dev.moataz.photoweather.di

import dev.moataz.photoweather.WeatherSharedViewModel
import dev.moataz.photoweather.datasource.RemoteDataSource
import dev.moataz.photoweather.network.Network
import dev.moataz.photoweather.repository.IWeatherRepo
import dev.moataz.photoweather.repository.WeatherRepo
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

private val network =  module { factory { Network.apiService }}

private val remoteModule = module { factory { RemoteDataSource(get()) } }

private val repositoryModule = module { single<IWeatherRepo> { WeatherRepo(get()) } }

private val viewModelModule = module {
viewModel { WeatherSharedViewModel(get()) }
}

fun getModules() : Array<Module>{

    return  arrayOf(
        network,
        remoteModule,
        repositoryModule,
        viewModelModule
    )
}
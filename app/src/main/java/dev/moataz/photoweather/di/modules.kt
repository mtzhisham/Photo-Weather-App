package dev.moataz.photoweather.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

private val network =  module {

}

private val remoteModule = module {  }

private val repositoryModule = module {  }

private val viewModelModule = module {

}

fun getModules() : Array<Module>{

    return  arrayOf(
        network,
        remoteModule,
        repositoryModule,
        viewModelModule
    )
}
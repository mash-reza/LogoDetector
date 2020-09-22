package com.festive.logodetector.di

import androidx.lifecycle.ViewModelProvider
import com.festive.logodetector.ViewModelProviderFactory
import dagger.Binds
import dagger.MapKey
import dagger.Module
import javax.inject.Singleton

@Module
abstract class
AppModule {

    @Binds
    abstract fun bindsViewModelProviderFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}
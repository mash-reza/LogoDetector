package com.festive.logodetector.di

import com.festive.logodetector.di.content.ContentModule
import com.festive.logodetector.di.content.ContentScope
import com.festive.logodetector.view.content.ContentActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContentScope
    @ContributesAndroidInjector(modules = [ContentModule::class])
    abstract fun contributeContentActivity():ContentActivity

}
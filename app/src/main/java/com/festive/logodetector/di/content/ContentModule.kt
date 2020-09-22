package com.festive.logodetector.di.content

import androidx.lifecycle.ViewModel
import com.festive.logodetector.di.ViewModelKey
import com.festive.logodetector.view.content.ContentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ContentModule {

    @ContentScope
    @Binds
    @IntoMap
    @ViewModelKey(ContentViewModel::class)
    abstract fun bindContentViewModel(contentViewModel: ContentViewModel):ViewModel
}
package com.dugue.canipark.di.modules

import com.dugue.canipark.ui.camera.CameraViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { CameraViewModel(get()) }
}
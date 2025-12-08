package com.study.presentation.feature.ble

import com.study.presentation.feature.ble.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val bleFeatureModule = module {
    viewModel{ HomeViewModel(get(), get()) }
}
package com.study.data

import com.study.data.repository.SpiroCalBleRepository
import com.study.data.repository.SpiroKitBleRepository
import com.study.data.source.ble.adapter.AndroidBleAdapterProvider
import com.study.data.source.ble.connector.AndroidBleGattManager
import com.study.data.source.ble.scanner.AndroidBleScanner
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { AndroidBleAdapterProvider(context = androidContext()) }
    single { AndroidBleScanner(get()) }
    single { AndroidBleGattManager(androidContext(), get()) }
    single { SpiroCalBleRepository(get(), get()) }
    single { SpiroKitBleRepository(get(), get()) }
}
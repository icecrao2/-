package com.study.androidstudyproject

import android.app.Application
import com.study.data.dataModule
import com.study.presentation.feature.ble.bleFeatureModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AndroidStudyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@AndroidStudyApplication)
            modules(
                dataModule,
                bleFeatureModule
            )
        }
    }
}
package com.study.androidstudyproject

import android.app.Application
import com.study.data.dataModule
import com.study.presentation.feature.eisenhower.di.eisenhowerFeatureModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AndroidStudyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        setKoin()
    }

    private fun setKoin() {
        startKoin {
            androidContext(this@AndroidStudyApplication)
            modules(
                dataModule,
                eisenhowerFeatureModule
            )
        }
    }

}
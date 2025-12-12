package com.study.data

import com.study.data.repository.TodoListRepository
import com.study.data.source.local.database.TodoEncryptedDatabaseProvider
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { TodoEncryptedDatabaseProvider(androidContext()) }
    single { TodoListRepository(get()) }
    single(createdAtStart = true) { AppInitializer(get()) }
}


private class AppInitializer(
    private val dbProvider: TodoEncryptedDatabaseProvider,
) {
    init {
        runBlocking {       // 뭔지 모름
            dbProvider.open("LOCAL_DEV_KEY_2025".toCharArray())
        }
    }
}
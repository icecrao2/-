package com.study.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.study.data.source.local.dao.TodoDao
import com.study.data.source.local.entity.TodoEntity

@Database(
    entities = [TodoEntity::class],
    version = 1
)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}
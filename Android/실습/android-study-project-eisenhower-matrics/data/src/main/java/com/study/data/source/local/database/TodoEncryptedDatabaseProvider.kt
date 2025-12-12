package com.study.data.source.local.database

import android.content.Context
import androidx.room.Room
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

class TodoEncryptedDatabaseProvider(
    private val context: Context
) {
    @Volatile               // 이해 안됨 예제에서 이렇게 써서 그냥 사용해봄 학습 필요
    private var db: TodoDatabase? = null

    fun open(passphraseChars: CharArray): TodoDatabase {
        db?.let { return it }

        val passphrase = SQLiteDatabase.getBytes(passphraseChars)
        val factory = SupportFactory(passphrase)

        val instance = Room.databaseBuilder(
            context,
            TodoDatabase::class.java,
            "todo_db"
        )
            .openHelperFactory(factory)
            .build()

        db = instance
        return instance
    }

    fun get(): TodoDatabase {
        return db ?: throw IllegalStateException("DB not opened yet")
    }

    fun close() {
        db?.close()
        db = null
    }
}
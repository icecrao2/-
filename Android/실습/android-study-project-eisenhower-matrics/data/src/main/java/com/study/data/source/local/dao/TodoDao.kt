package com.study.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.study.data.source.local.entity.TodoEntity

@Dao
interface TodoDao {
    @Insert
    suspend fun insert(todo: TodoEntity)

    @Update
    suspend fun update(vararg todo: TodoEntity)

    @Delete
    suspend fun delete(todo:  TodoEntity)

    @Query("SELECT * FROM todo")
    suspend fun getAll(): List<TodoEntity>

    @Query("SELECT * FROM todo ORDER by deadline asc")
    suspend fun getTodosOrderByDeadlineAsc(): List<TodoEntity>

    @Query("SELECT * FROM todo ORDER by deadline desc")
    suspend fun getTodosOrderByDeadlineDesc(): List<TodoEntity>
}
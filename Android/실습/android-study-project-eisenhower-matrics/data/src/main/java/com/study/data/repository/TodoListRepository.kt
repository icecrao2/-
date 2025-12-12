package com.study.data.repository

import com.study.data.source.local.extension.getTodo
import com.study.data.source.local.extension.getTodoEntity
import com.study.data.source.local.dao.TodoDao
import com.study.data.source.local.database.TodoEncryptedDatabaseProvider
import com.study.domain.model.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TodoListRepository(
    dbProvider: TodoEncryptedDatabaseProvider
) {
    private val todoDao = dbProvider.get().todoDao()
    private val _todoList = MutableStateFlow<List<Todo>>(emptyList())
    val todoList: StateFlow<List<Todo>> = _todoList

    suspend fun addTodo(todo: Todo) {
        todoDao.insert(todo.getTodoEntity())
        loadTodo()
    }

    suspend fun editTodo(updated: Todo) {
        todoDao.update(updated.getTodoEntity())
        loadTodo()
    }

    suspend fun removeTodo(todo: Todo) {
        todoDao.delete(todo.getTodoEntity())
        loadTodo()
    }

    suspend fun loadTodo() {
        _todoList.value = todoDao.getTodosOrderByDeadlineAsc().map { it.getTodo() }
    }
}
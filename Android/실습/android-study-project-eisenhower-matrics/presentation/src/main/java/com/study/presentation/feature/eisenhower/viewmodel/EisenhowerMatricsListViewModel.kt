package com.study.presentation.feature.eisenhower.viewmodel

import BaseViewModel
import androidx.lifecycle.viewModelScope
import com.study.data.repository.TodoListRepository
import com.study.domain.model.EisenhowerType
import com.study.domain.model.Todo
import com.study.presentation.common.base.UiEffect
import com.study.presentation.common.base.UiEvent
import com.study.presentation.common.base.UiOperation
import kotlinx.coroutines.launch

sealed interface EisenhowerMatricsListEvent : UiEvent {
    class Load() : EisenhowerMatricsListEvent
}

sealed interface EisenhowerMatricsListOperation : UiOperation {
    class Load() : EisenhowerMatricsListOperation
}

sealed interface EisenhowerMatricsListEffect : UiEffect {}


class EisenhowerMatricsListViewModel(private val todoListRepository: TodoListRepository) :
    BaseViewModel<
            EisenhowerMatricsListEvent,
            List<Todo>,
            EisenhowerMatricsListOperation,
            EisenhowerMatricsListEffect>() {

    init {
        viewModelScope.launch {
            observeTodoList()
        }
    }

    private suspend fun observeTodoList() {
        todoListRepository.todoList.collect { new ->
            updateData { new }
        }
    }

    override fun createInitialData(): List<Todo> = try {
        todoListRepository.todoList.value
    } catch (e: Exception) {
        emptyList()
    }

    override fun handleEvent(event: EisenhowerMatricsListEvent) {
        when (event) {
            is EisenhowerMatricsListEvent.Load -> viewModelScope.launch { loadTodo() }
        }
    }

    val urgentImportantTodoList: List<Todo>
        get() = getTodoListByEisenhowerType(EisenhowerType.URGENT_IMPORTANT)

    val notUrgentImportantTodoList: List<Todo>
        get() = getTodoListByEisenhowerType(EisenhowerType.NOT_URGENT_IMPORTANT)

    val notUrgentNotImportantTodoList: List<Todo>
        get() = getTodoListByEisenhowerType(EisenhowerType.NOT_URGENT_NOT_IMPORTANT)

    val urgentNorImportantTodoList: List<Todo>
        get() = getTodoListByEisenhowerType(EisenhowerType.URGENT_NOT_IMPORTANT)


    private fun getTodoListByEisenhowerType(eisenhowerType: EisenhowerType): List<Todo> =
        currentData.filter { todo -> todo.eisenhowerType == eisenhowerType }

    private suspend fun loadTodo() {
        try {
            todoListRepository.loadTodo()
            setSuccess(
                data = todoListRepository.todoList.value,
                operation = EisenhowerMatricsListOperation.Load()
            )
        } catch (e: Exception) {
            setFail(operation = EisenhowerMatricsListOperation.Load())
        }
    }
}
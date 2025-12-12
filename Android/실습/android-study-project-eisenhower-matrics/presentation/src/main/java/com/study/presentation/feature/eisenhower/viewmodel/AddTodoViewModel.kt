package com.study.presentation.feature.eisenhower.viewmodel

import androidx.lifecycle.viewModelScope
import com.study.data.repository.TodoListRepository
import kotlinx.coroutines.launch

class AddTodoViewModel(todoListRepository: TodoListRepository) :
    TodoFormViewModel(todoListRepository) {
    override fun submitTodo() {
        viewModelScope.launch {
            addTodo()
        }
    }

    private suspend fun addTodo() {
        try {
            refreshUiState()
            todoListRepository.addTodo(currentData.getTodo())
            clear()
            setSuccess(operation = TodoFormOperation.Submit())
        } catch (e: Exception) {
            sendEffect(TodoFormEffect.ShowToast("잘못된 규격입니다. 다시 입력해주세요"))
            setFail(operation = TodoFormOperation.Submit())
        }
    }
}
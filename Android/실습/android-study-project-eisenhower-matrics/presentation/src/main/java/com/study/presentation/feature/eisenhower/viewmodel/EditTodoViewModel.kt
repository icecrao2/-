package com.study.presentation.feature.eisenhower.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.study.data.repository.TodoListRepository
import com.study.domain.model.Todo
import kotlinx.coroutines.launch

sealed interface EditTodoFormEvent : TodoFormEvent {
    data class SelectTodo(val todo: Todo) : EditTodoFormEvent
    class DeselectTodo() : EditTodoFormEvent
    class RemoveTodo() : EditTodoFormEvent
}

sealed interface EditTodoOperation : TodoFormOperation {
    class Select() : TodoFormOperation
    class Remove() : TodoFormOperation
}

class EditTodoViewModel(todoListRepository: TodoListRepository) :
    TodoFormViewModel(todoListRepository) {
    private val _isSelected: MutableLiveData<Boolean> = MutableLiveData(false)
    val isSelected: LiveData<Boolean> = _isSelected

    override fun submitTodo() {
        viewModelScope.launch {
            editTodo()
        }
    }

    private suspend fun editTodo() {
        try {
            refreshUiState()
            todoListRepository.editTodo(currentData.getTodo())
            clear()
            setSuccess(operation = TodoFormOperation.Submit())
        } catch (_: Exception) {
            sendEffect(TodoFormEffect.ShowToast("잘못된 규격입니다. 다시 입력해주세요"))
            setFail(operation = TodoFormOperation.Submit())
        }
    }

    override fun handleEvent(event: TodoFormEvent) {
        when (event) {
            is EditTodoFormEvent.SelectTodo -> selectTodo(event.todo)
            is EditTodoFormEvent.DeselectTodo -> deselectTodo()
            is EditTodoFormEvent.RemoveTodo -> removeTodo()
            else -> Unit
        }
        super.handleEvent(event)
    }

    private fun selectTodo(todo: Todo) {
        try {
            _name.value = todo.name
            _daysRequired.value = todo.estimatedDaysRequired.toString()
            _deadlineText.value = todo.deadlineDate.toString()
            _isImportant.value = todo.isImportant
            _eisenhowerType.value = todo.eisenhowerType
            _description.value = todo.description
            refreshUiState(id = todo.id)
            setIsSelected(true)
            setSuccess(operation = EditTodoOperation.Select())
        } catch (e: Exception) {
            setFail(operation = EditTodoOperation.Select())
        }
    }

    private fun deselectTodo() {
        setIsSelected(false)
        clear()
    }

    private fun removeTodo() {
        viewModelScope.launch {
            removeTodoData()
        }
    }

    private fun setIsSelected(isSelected: Boolean) {
        if (_isSelected.value == !isSelected) _isSelected.value = isSelected
    }

    private suspend fun removeTodoData() {
        try {
            todoListRepository.removeTodo(currentData.getTodo())
            setSuccess(operation = EditTodoOperation.Remove())
        } catch (_: Exception) {
            sendEffect(TodoFormEffect.ShowToast("삭제에 실패했습니다"))
            setFail(operation = EditTodoOperation.Remove())
        }
    }
}
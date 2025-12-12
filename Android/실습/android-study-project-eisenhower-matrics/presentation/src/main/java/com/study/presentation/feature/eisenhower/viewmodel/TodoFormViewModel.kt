package com.study.presentation.feature.eisenhower.viewmodel

import BaseViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.study.data.repository.TodoListRepository
import com.study.domain.model.EisenhowerType
import com.study.presentation.common.base.UiEffect
import com.study.presentation.common.base.UiEvent
import com.study.presentation.common.base.UiOperation
import com.study.presentation.feature.eisenhower.data.TodoFormUiState
import java.time.LocalDate

sealed interface TodoFormEvent : UiEvent {
    class Submit() : TodoFormEvent
    data class ChangeName(val name: String) : TodoFormEvent
    data class ChangeDaysRequired(val daysRequired: String) : TodoFormEvent
    data class ChangeDeadline(val deadline: String) : TodoFormEvent
    data class ChangeIsImportant(val isImportant: Boolean) : TodoFormEvent
    data class ChangeDescription(val description: String) : TodoFormEvent
    class OnSucceedSubmit() : TodoFormEvent
}

sealed interface TodoFormOperation : UiOperation {
    class Submit() : TodoFormOperation
}

sealed interface TodoFormEffect : UiEffect {
    data class ShowToast(val message: String) : TodoFormEffect
}

abstract class TodoFormViewModel(protected val todoListRepository: TodoListRepository) :
    BaseViewModel<
            TodoFormEvent,
            TodoFormUiState,
            TodoFormOperation,
            TodoFormEffect>() {

    protected val _name = MutableLiveData(currentData.name)
    val name: LiveData<String> = _name
    protected val _daysRequired = MutableLiveData(currentData.daysRequired)
    val daysRequired: LiveData<String> = _daysRequired
    protected val _deadlineText = MutableLiveData(currentData.deadlineText)
    val deadlineText: LiveData<String> = _deadlineText
    protected val _isImportant = MutableLiveData(currentData.isImportant)
    val isImportant: LiveData<Boolean> = _isImportant
    protected val _eisenhowerType = MutableLiveData(currentData.manualEisenhowerType)
    val eisenhowerType: LiveData<EisenhowerType> = _eisenhowerType
    protected val _description = MutableLiveData(currentData.description)
    val description: LiveData<String> = _description

    override fun createInitialData(): TodoFormUiState = TodoFormUiState()

    override fun handleEvent(event: TodoFormEvent) {
        when (event) {
            is TodoFormEvent.Submit -> submitTodo()
            is TodoFormEvent.ChangeName -> onChangedName(event.name)
            is TodoFormEvent.ChangeDaysRequired -> onChangeDaysRequired(event.daysRequired)
            is TodoFormEvent.ChangeDeadline -> onChangedDeadline(event.deadline)
            is TodoFormEvent.ChangeIsImportant -> onChangedIsImportant(event.isImportant)
            is TodoFormEvent.ChangeDescription -> onChangedDescription(event.description)
            is TodoFormEvent.OnSucceedSubmit -> clear()
            else -> Unit
        }
    }

    protected abstract fun submitTodo()

    private fun onChangedName(name: String) {
        _name.value = name;
    }

    private fun onChangeDaysRequired(daysRequired: String) {
        _daysRequired.value = daysRequired;
        updateEisenhowerType()
    }

    private fun onChangedDeadline(deadline: String) {
        _deadlineText.value = deadline;
        updateEisenhowerType()
    }

    private fun onChangedIsImportant(isImportant: Boolean) {
        _isImportant.value = isImportant;
        updateEisenhowerType()
    }

    private fun onChangedDescription(description: String) {
        _description.value = description;
    }

    protected fun updateEisenhowerType() {
        val eisenhowerType = EisenhowerType.from(
            isImportant = isImportant.value ?: false,
            deadlineDate = try {
                TodoFormUiState(deadlineText = deadlineText.value.toString()).getDeadline()
            } catch (_: Exception) {
                LocalDate.now()
            },
            estimatedDaysRequired = try {
                daysRequired.value!!.toInt()
            } catch (_: Exception) {
                0
            },
        )
        _eisenhowerType.value = eisenhowerType
    }

    protected fun refreshUiState(
        id: String? = null
    ) {
        setIdle()
        updateData { state ->
            state.copy(
                id = id ?: state.id,
                name = name.value!!,
                daysRequired = daysRequired.value!!,
                deadlineText = deadlineText.value!!,
                isImportant = isImportant.value!!,
                description = description.value!!
            )
        }
    }

    protected fun clear() {
        _name.value = ""
        _daysRequired.value = ""
        _deadlineText.value = ""
        _isImportant.value = false
        _eisenhowerType.value = EisenhowerType.NOT_URGENT_NOT_IMPORTANT
        _description.value = ""
        refreshUiState(id = "")
    }
}
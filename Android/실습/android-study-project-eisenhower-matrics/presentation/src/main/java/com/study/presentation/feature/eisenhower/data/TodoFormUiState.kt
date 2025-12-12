package com.study.presentation.feature.eisenhower.data

import com.study.domain.model.EisenhowerType
import com.study.domain.model.Todo
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class TodoFormUiState(
    val id: String = "",
    val name: String = "",
    val daysRequired: String = "",
    val deadlineText: String = "",
    val isImportant: Boolean = false,
    val manualEisenhowerType: EisenhowerType = EisenhowerType.NOT_URGENT_NOT_IMPORTANT,
    val description: String = ""
) {

    fun getDeadline(): LocalDate {
        return try {
            LocalDate.parse(deadlineText, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } catch (e: DateTimeParseException) {
            throw Exception("wrong deadline format ")
        }
    }

    fun getTodo(): Todo {
        val todo = Todo(
            id = id,
            name = name,
            estimatedDaysRequired = daysRequired.toInt(),
            deadlineDate = getDeadline(),
            isImportant = isImportant,
            description = description,
        )
        todo.validate()
        return todo
    }
}
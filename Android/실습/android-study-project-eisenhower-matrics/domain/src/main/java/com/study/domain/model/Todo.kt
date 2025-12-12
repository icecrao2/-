package com.study.domain.model

import java.time.LocalDate
import java.util.UUID

data class Todo(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val estimatedDaysRequired: Int,
    val deadlineDate: LocalDate,
    val isImportant: Boolean,
    val manualEisenhowerType: EisenhowerType? = null,
    val description: String
) {
    val eisenhowerType: EisenhowerType =
        manualEisenhowerType
            ?: EisenhowerType.from(
                isImportant = isImportant,
                deadlineDate = deadlineDate,
                estimatedDaysRequired = estimatedDaysRequired
            )

    fun validate() {
        val trimmedName = name.trim()
        require(trimmedName.isNotEmpty()) {
            "name must not be blank"
        }

        require(estimatedDaysRequired >= 1) {
            "estimatedDaysRequired must be at least 1"
        }

        val today = LocalDate.now()
        require(!deadlineDate.isBefore(today)) {
            "deadlineDate must not be before today (today = $today, given = $deadlineDate)"
        }
    }
}
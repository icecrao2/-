package com.study.domain.model

import java.time.Clock
import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class EisenhowerType(val code: String) {
    URGENT_IMPORTANT("URGENT_IMPORTANT"),
    NOT_URGENT_IMPORTANT("NOT_URGENT_IMPORTANT"),
    URGENT_NOT_IMPORTANT("URGENT_NOT_IMPORTANT"),
    NOT_URGENT_NOT_IMPORTANT("NOT_URGENT_NOT_IMPORTANT");

    companion object {
        fun from(
            isImportant: Boolean,
            deadlineDate: LocalDate,
            estimatedDaysRequired: Int,
            clock: Clock = Clock.systemDefaultZone()
        ): EisenhowerType {
            val today = LocalDate.now(clock)
            val daysLeft = ChronoUnit.DAYS.between(today, deadlineDate)
            val isUrgent = daysLeft <= estimatedDaysRequired

            return when {
                isImportant && isUrgent -> URGENT_IMPORTANT
                isImportant && !isUrgent -> NOT_URGENT_IMPORTANT
                !isImportant && isUrgent -> URGENT_NOT_IMPORTANT
                else -> NOT_URGENT_NOT_IMPORTANT
            }
        }

        fun getEisenhowerTypeByString(code: String): EisenhowerType {
            return when(code) {
                URGENT_IMPORTANT.code -> URGENT_IMPORTANT
                NOT_URGENT_IMPORTANT.code -> NOT_URGENT_IMPORTANT
                URGENT_NOT_IMPORTANT.code -> URGENT_NOT_IMPORTANT
                NOT_URGENT_NOT_IMPORTANT.code -> NOT_URGENT_NOT_IMPORTANT
                else -> throw Exception("wrong eisenhower type code")
            }
        }
    }
}
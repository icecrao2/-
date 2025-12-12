package com.study.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.study.domain.model.EisenhowerType
import java.time.LocalDate

@Entity(
    tableName = "todo",
    indices = [
        Index(value = ["eisenhower_type", "deadline"])
    ]
)
data class TodoEntity(
    @PrimaryKey val id: String,
    val name: String,
    @ColumnInfo(name = "days_required") val estimatedDaysRequired: Int,
    @ColumnInfo(name = "eisenhower_type") val eisenhowerType: String,
    @ColumnInfo(name = "deadline") val deadline: Long,
    @ColumnInfo(name = "important") val isImportant: Boolean,
    val description: String
) {
    @Ignore
    val deadlineDate: LocalDate = LocalDate.ofEpochDay(deadline)
    @Ignore
    val manualEisenhowerType: EisenhowerType =
        EisenhowerType.getEisenhowerTypeByString(eisenhowerType)
}
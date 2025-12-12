package com.study.data.source.local.extension

import com.study.data.source.local.entity.TodoEntity
import com.study.domain.model.Todo

fun Todo.getTodoEntity(): TodoEntity = TodoEntity(
    id = id,
    name = name,
    estimatedDaysRequired = estimatedDaysRequired,
    eisenhowerType = eisenhowerType.code,
    deadline = deadlineDate.toEpochDay(),
    isImportant = isImportant,
    description = description
)

fun TodoEntity.getTodo(): Todo = Todo(
    id = id,
    name = name,
    estimatedDaysRequired = estimatedDaysRequired,
    deadlineDate = deadlineDate,
    isImportant = isImportant,
    manualEisenhowerType = manualEisenhowerType,
    description = description
)
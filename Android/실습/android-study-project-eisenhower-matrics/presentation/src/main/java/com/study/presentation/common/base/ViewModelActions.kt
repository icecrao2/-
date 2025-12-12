package com.study.presentation.common.base

interface UiEvent
interface UiEffect
interface UiOperation
sealed class UiStatus<out T, out O> {
    data class Idle<out T, out O>(val data: T? = null) : UiStatus<T, O>()
    data class Success<out T, out O>(
        val data: T,
        val operation: O
    ) : UiStatus<T, O>()

    data class Fail<out T, out O>(
        val message: String? = null,
        val data: T? = null,
        val throwable: Throwable? = null,
        val operation: O
    ) : UiStatus<T, O>()
}
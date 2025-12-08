package com.study.domain.model

enum class SpirokitLifecycleMessage(
    val code: String
) {
    PREPARING("2"),
    TERMINATING("3");

    companion object {
        fun getLifecycleMessage(raw: String): SpirokitLifecycleMessage? =
            entries.firstOrNull { it.code == raw }
    }
}
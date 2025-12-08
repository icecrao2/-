package com.study.domain.model

enum class SpirokitBatteryStatus(
    val code: String
) {
    Idle("I"),
    Start("S"),
    Run("R"),
    Stop("O"),
    Off("F");

    companion object {
        fun getBatteryStatus(message: String): SpirokitBatteryStatus? {
            return SpirokitBatteryStatus.entries.firstOrNull { status ->
                message.contains(status.code)
            }
        }
    }
}
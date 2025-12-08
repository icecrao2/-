package com.study.domain.model

sealed class SpiroKitCommand(
    private val prefix: String,
) {
    private var suffix: String = ""
    fun getCommand(): String = "$prefix$suffix"
    fun setSuffix(suffix: String) { this.suffix = suffix }
}

class IntervalCommand: SpiroKitCommand("dely")
class TurnOnCommand: SpiroKitCommand("mstr")
class TurnOffCommand: SpiroKitCommand("mstp")
class ReceiveBatteryCommand: SpiroKitCommand("batt1")
class IgnoreBatteryCommand: SpiroKitCommand("batt0")
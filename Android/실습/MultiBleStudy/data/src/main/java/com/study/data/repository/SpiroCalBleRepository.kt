package com.study.data.repository

import com.study.data.source.ble.connector.AndroidBleGattManager
import com.study.data.source.ble.scanner.AndroidBleScanner
import com.study.domain.model.DeviceType
import com.study.domain.model.EnvironmentData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.text.split

class SpiroCalBleRepository(
    scanner: AndroidBleScanner,
    connector: AndroidBleGattManager,
) : BaseBleRepository(
    scanner,
    connector,
    DeviceType.SpiroCal.prefixName
) {

    fun observeEnvironmentData(): Flow<EnvironmentData> {
        val flow = getDataFlow() ?: throw IllegalStateException("Not Notification")
        return flow
            .map { value ->
                val valueList = value.split(" ")

                val temperature = valueList[0].substring(1)
                val humidity = valueList[1].substring(1)
                val pressure = valueList[2].substring(1)

                EnvironmentData(
                    temperature = temperature.toDouble(),
                    humidity = humidity.toInt(),
                    pressure = pressure.toInt()
                )
            }
    }

    fun observeBatteryData(): Flow<String> {
        val flow = getDataFlow() ?: throw IllegalStateException("Not Notification")
        return flow
            .map { value -> value.split(" ")[3].substring(1, 3) }
    }
}
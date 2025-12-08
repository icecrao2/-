package com.study.presentation.feature.ble

import com.study.data.source.ble.model.BleDevice
import com.study.domain.model.Device
import com.study.domain.model.DeviceType

fun BleDevice.toDevice(type: DeviceType) = Device(
    address = address,
    name = name,
    type = type
)
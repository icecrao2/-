package com.study.data.extension

import com.study.data.source.ble.model.BleDevice
import com.study.domain.model.Device


fun Device.toBleDevice() = BleDevice(
    address = address,
    name = ""
)
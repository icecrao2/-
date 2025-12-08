package com.study.data.source.ble.model

import java.util.UUID

enum class BleUuid(
    val uuid: UUID
) {
    CccdUuid(uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")),
    UartServiceUuid(uuid = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")),
    UartTxCharUuid(uuid = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")),
    UartRxCharUuid(UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e"))
}
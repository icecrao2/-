package com.study.data.source.ble.connector

interface BluetoothListener {
    fun onRawDataReceive(data: ByteArray)
}
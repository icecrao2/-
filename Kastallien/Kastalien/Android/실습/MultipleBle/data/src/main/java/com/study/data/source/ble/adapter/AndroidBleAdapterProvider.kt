package com.study.data.source.ble.adapter

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context

class AndroidBleAdapterProvider(
    private val context: Context
) {
    private val bluetoothManager by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }

    val adapter: BluetoothAdapter? get() = bluetoothManager.adapter

    fun isBluetoothEnabled(): Boolean = adapter?.isEnabled == true
}
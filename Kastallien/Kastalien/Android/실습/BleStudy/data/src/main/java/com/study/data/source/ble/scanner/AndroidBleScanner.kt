package com.study.data.source.ble.scanner

import android.annotation.SuppressLint
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.study.data.source.ble.adapter.AndroidBleAdapterProvider

class AndroidBleScanner(
    private val provider: AndroidBleAdapterProvider
) {
    private val scanner: BluetoothLeScanner by lazy {
        if(provider.adapter == null) throw IllegalStateException("BLE scanner not available")
        provider.adapter!!.bluetoothLeScanner
    }

    private val handler: Handler by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Handler.createAsync(Looper.getMainLooper())
        } else {
            Handler(Looper.getMainLooper())
        }
    }

    private val scanSetting = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()
    private var scanning = false
    private var callback: AndroidBleScanCallback? = null

    fun scanDevice(filter: List<ScanFilter>? = null, includedName: String? = null): AndroidBleScanCallback? {
        if (!scanning) {
            scheduleStopScan()
            return startScan(filter, includedName)
        } else {
            stopScan()
            return null
        }
    }

    private fun scheduleStopScan() {
        val scanPeriod = 10000L
        handler.postDelayed({
            stopScan()
        }, scanPeriod)
    }

    @SuppressLint("MissingPermission")
    private fun startScan(filter: List<ScanFilter>? = null, includedName: String? = null): AndroidBleScanCallback {
        scanning = true
        callback = AndroidBleScanCallback(includedName)
        scanner.startScan(filter, scanSetting, callback)
        return callback!!
    }

    @SuppressLint("MissingPermission")
    private fun stopScan() {
        scanning = false
        scanner.stopScan(callback)
        callback = null
    }
}
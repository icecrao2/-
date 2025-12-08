package com.study.data.source.ble.scanner

import android.annotation.SuppressLint
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import com.study.data.source.ble.model.BleDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class AndroidBleScanCallback(
    private val includedName: String?
): ScanCallback() {
    private val _scannedDevice: MutableStateFlow<Set<BleDevice>> = MutableStateFlow(emptySet())
    val scannedDevice: StateFlow<Set<BleDevice>> = _scannedDevice

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        result?.device?.let { device ->
            _scannedDevice.update { devices ->
                val name: String = try { @SuppressLint("MissingPermission") device.name!! } catch(_: Exception) { "" }       //TODO: 나중에 Extension으로 따로 빼자
                if(includedName == null) {
                    devices + BleDevice(name = name, address = device.address)
                } else {
                    val isContains = name.contains(includedName)
                    if(isContains) {
                        devices + BleDevice(name = name, address = device.address)
                    } else {
                        devices
                    }
                }
            }
        }
    }
}
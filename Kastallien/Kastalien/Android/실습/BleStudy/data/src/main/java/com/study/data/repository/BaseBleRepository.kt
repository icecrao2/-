package com.study.data.repository

import android.Manifest
import android.bluetooth.le.ScanFilter
import androidx.annotation.RequiresPermission
import com.study.data.extension.toBleDevice
import com.study.data.source.ble.connector.AndroidBleGattManager
import com.study.data.source.ble.model.BleDevice
import com.study.data.source.ble.scanner.AndroidBleScanner
import com.study.domain.model.BleConnectionStatus
import com.study.domain.model.Device
import com.study.domain.model.SpiroKitCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

abstract class BaseBleRepository(
    protected val scanner: AndroidBleScanner,
    protected val gattManager: AndroidBleGattManager,
    protected val includedName: String
) {
    protected var connectedDevice: Device? = null

    val connectionStatus: Flow<BleConnectionStatus>? get() = getDeviceConnectionStatus()
    val rawDataFlow: Flow<String>? get() = getDataFlow()
    val isConnected: Boolean get() = isDeviceConnected()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var connectionStatusJob: Job? = null

    fun scanDevice(): StateFlow<Set<BleDevice>>?  {
        return scanner.scanDevice(
            filter = listOf(ScanFilter.Builder().build()),
            includedName = includedName)?.scannedDevice
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    open fun connectDevice(device: Device): Flow<String> {
        if(isConnected) return rawDataFlow!!

        gattManager.connect(device.toBleDevice())
        connectedDevice = device
        observeConnectionStatus()
        return getDataFlow()!!
    }

    private fun observeConnectionStatus() {
        connectionStatusJob?.cancel()
        connectionStatusJob = scope.launch {
            connectionStatus?.collect { status ->
                when(status) {
                    BleConnectionStatus.NotConnected -> clear()
                    BleConnectionStatus.Pending -> Unit
                    BleConnectionStatus.Connected -> Unit
                }
            }
        }
    }

    protected fun getDataFlow(): Flow<String>? {
        if(connectedDevice == null) return null
        return gattManager
            .getGattCallback(connectedDevice!!.toBleDevice())
            ?.rawDataDataCallback
            ?.map { data -> data.toString(Charsets.UTF_8) }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnectDevice() {
        gattManager.disconnect(connectedDevice!!.toBleDevice())
        clear()
    }

    protected open fun clear() {
        connectedDevice = null
        connectionStatusJob?.cancel()
        connectionStatusJob = null
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun sendCommand(command: SpiroKitCommand) {
        if(connectedDevice == null) return;
        gattManager.sendCommand(connectedDevice!!.toBleDevice(), command.getCommand())
    }

    private fun isDeviceConnected() : Boolean {
        if (connectedDevice == null) return false
        return gattManager.isConnected(device = connectedDevice!!.toBleDevice())
    }

    private fun getDeviceConnectionStatus(): Flow<BleConnectionStatus>? {
        if(connectedDevice == null) return null
        return gattManager.getGattCallback(connectedDevice!!.toBleDevice())?.connectionStatus
    }
}
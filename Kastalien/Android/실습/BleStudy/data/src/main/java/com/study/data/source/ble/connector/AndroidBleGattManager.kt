package com.study.data.source.ble.connector

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import com.study.data.source.ble.adapter.AndroidBleAdapterProvider
import com.study.data.source.ble.model.BleDevice
import com.study.domain.model.BleConnectionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.collections.set

class AndroidBleGattManager(
    private val context: Context,
    private val adapterProvider: AndroidBleAdapterProvider
) {
    private val adapter: BluetoothAdapter by lazy {
        adapterProvider.adapter ?: throw IllegalStateException("BLE connector not available")
    }
    private val gattMap = mutableMapOf<String, BluetoothGatt>()
    private val gattCallbackMap = mutableMapOf<String, AndroidBleGattCallback>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val connectionStatusJob = mutableMapOf<String, Job>()

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connect(device: BleDevice): AndroidBleGattCallback {
        if(gattMap.containsKey(device.address)) {
            return gattCallbackMap[device.address]!!
        }
        saveAndroidBleGattCallback(device)
        connectDevice(device)
        observeConnectionStatus(device)
        return gattCallbackMap[device.address]!!
    }

    private fun saveAndroidBleGattCallback(device: BleDevice) {
        gattCallbackMap[device.address] = AndroidBleGattCallback()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun connectDevice(device: BleDevice) {
        val bleDevice = adapter.getRemoteDevice(device.address)
        gattMap[device.address] = bleDevice.connectGatt(context, false, gattCallbackMap[device.address])
    }

    private fun observeConnectionStatus(device: BleDevice) {
        connectionStatusJob.remove(device.address)?.cancel()

        val job = scope.launch {
            gattCallbackMap[device.address]?.connectionStatus?.collect { status ->
                when(status) {
                    BleConnectionStatus.NotConnected -> clear(device)
                    BleConnectionStatus.Pending -> Unit
                    BleConnectionStatus.Connected -> Unit
                }
            }
        }
        connectionStatusJob[device.address] = job
    }

    fun isConnected(device: BleDevice): Boolean {
        return gattMap.containsKey(device.address)
    }

    fun getGattCallback(device: BleDevice): AndroidBleGattCallback? {
        return gattCallbackMap[device.address]
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect(device: BleDevice) {
        gattMap[device.address]?.disconnect()
        clear(device)
    }

    private fun clear(device: BleDevice) {
        connectionStatusJob.remove(device.address)?.cancel()
        gattMap.remove(device.address)
        gattCallbackMap.remove(device.address)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun sendCommand(device: BleDevice, command: String) {
        val gatt = gattMap[device.address] ?: return
        val characteristic = gattCallbackMap[device.address]?.writeCharacteristic ?: return
        val bytes = command.toByteArray()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gatt.writeCharacteristic(characteristic, bytes, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
        } else {
            characteristic.value = bytes
            characteristic.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            gatt.writeCharacteristic(characteristic)
        }
    }
}
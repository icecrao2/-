package com.study.data.source.ble.connector

import android.Manifest
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.os.Build
import androidx.annotation.RequiresPermission
import com.study.data.source.ble.model.BleUuid
import com.study.domain.model.BleConnectionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update

const val SUBSCRIBE_TIME_OUT = 5000L

class AndroidBleGattCallback(): BluetoothGattCallback() {
    private var gatt: BluetoothGatt? = null
    private var rawDataListener: BluetoothListener? = null

    private var _writeCharacteristic: BluetoothGattCharacteristic? = null
    val writeCharacteristic get() = _writeCharacteristic

    private val _connectionState = MutableStateFlow(BleConnectionStatus.Pending)
    val connectionStatus: Flow<BleConnectionStatus> = _connectionState

    val rawDataDataCallback: Flow<ByteArray> = callbackFlow {
        rawDataListener = object : BluetoothListener {
            override fun onRawDataReceive(data: ByteArray) {
                trySendBlocking(data)
            }
        }
        awaitClose {
            rawDataListener = null
        }
    }.shareIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(SUBSCRIBE_TIME_OUT),
        replay = 0
    )


    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
            println("connected!")
            gatt.discoverServices()
            this.gatt = gatt
            _connectionState.update { BleConnectionStatus.Pending }
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            println("disconnectied!")
            _connectionState.update { BleConnectionStatus.NotConnected }
            gatt.close()
            this.gatt = null
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        println("onServicesDiscovered status=$status")

        if (status != BluetoothGatt.GATT_SUCCESS) {
            println("onServicesDiscovered 실패 status=$status")
            return
        }

        // 1) UART 서비스 찾기
        val service = gatt.getService(BleUuid.UartServiceUuid.uuid)
        if (service == null) {
            println("UART service not found")
            return
        }

        // 2) Notify 받을 TX characteristic 찾기
        val txChar = service.getCharacteristic(BleUuid.UartTxCharUuid.uuid)
        if (txChar == null) {
            println("UART TX characteristic not found")
            return
        }

        _writeCharacteristic = service.getCharacteristic(BleUuid.UartRxCharUuid.uuid)
        if (_writeCharacteristic == null) {
            println("UART TX characteristic not found")
            return
        }

        val props = txChar.properties
        val canNotify = (props and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0
        val canIndicate = (props and BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0

        println("UART TX props notify=$canNotify, indicate=$canIndicate")

        if (!canNotify && !canIndicate) {
            println("UART TX cannot notify/indicate")
            return
        }

        // 3) 로컬 notification 플래그 켜기
        val setOk = gatt.setCharacteristicNotification(txChar, true)
        println("setCharacteristicNotification(TX) = $setOk")
        if (!setOk) return

        // 4) CCCD descriptor 에 실제로 값 쓰기
        val cccd = txChar.getDescriptor(BleUuid.CccdUuid.uuid)
        if (cccd == null) {
            println("no CCCD on TX characteristic")
            return
        }

        val value: ByteArray = if (canNotify) {
            BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        } else {
            BluetoothGattDescriptor.ENABLE_INDICATION_VALUE
        }

        val writeResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gatt.writeDescriptor(cccd, value)
        } else {
            cccd.value = value
            gatt.writeDescriptor(cccd)
        }


        println("writeDescriptor(TX) result=$writeResult")
        _connectionState.update { BleConnectionStatus.Connected }
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray,
        status: Int
    ) { }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray
    ) {
        if (this.gatt == null || this.gatt !== gatt) return
        rawDataListener?.onRawDataReceive(value)
    }

    @Deprecated("Deprecated in API 33")
    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?,
        characteristic: BluetoothGattCharacteristic?
    ) {
        val value = characteristic?.value ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return
        if (this.gatt == null || this.gatt !== gatt) return
        rawDataListener?.onRawDataReceive(value)
    }

    override fun onCharacteristicWrite(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        status: Int
    ) { }
}
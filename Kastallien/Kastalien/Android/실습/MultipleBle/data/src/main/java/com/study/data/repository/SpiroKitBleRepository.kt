package com.study.data.repository

import android.Manifest
import androidx.annotation.RequiresPermission
import com.study.data.source.ble.connector.AndroidBleGattManager
import com.study.data.source.ble.scanner.AndroidBleScanner
import com.study.domain.model.Device
import com.study.domain.model.DeviceType
import com.study.domain.model.SpirokitBatteryStatus
import com.study.domain.model.SpirokitLifecycleMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SpiroKitBleRepository(
    scanner: AndroidBleScanner,
    connector: AndroidBleGattManager,
): BaseBleRepository(
    scanner,
    connector,
    DeviceType.SpiroKit.prefixName
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var lifeCycleJob: Job? = null

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun connectDevice(device: Device): Flow<String> {
        val flow = super.connectDevice(device)
            .filter { value -> SpirokitBatteryStatus.getBatteryStatus(value) == null }
            .filter { value -> SpirokitLifecycleMessage.getLifecycleMessage(value) == null }
        listenLifecycleMessage()
        return flow
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun listenLifecycleMessage() {
        lifeCycleJob?.cancel()
        lifeCycleJob = scope.launch {
            observeLifecycleMessage().collect  { status ->
                when(status) {
                    SpirokitLifecycleMessage.PREPARING -> Unit
                    SpirokitLifecycleMessage.TERMINATING -> disconnectDevice()
                }
            }
        }
    }

    override fun clear() {
        super.clear()
        lifeCycleJob?.cancel()
        lifeCycleJob = null
    }

    fun observeBatteryData(): Flow<SpirokitBatteryStatus> {
        val flow = getDataFlow() ?: throw IllegalStateException("Not Notification")
        return flow
            .filter { value -> SpirokitBatteryStatus.getBatteryStatus(value) != null }
            .map { value ->  SpirokitBatteryStatus.getBatteryStatus(value)!! }
    }

    fun observeLifecycleMessage(): Flow<SpirokitLifecycleMessage> {
        val flow = getDataFlow() ?: throw IllegalStateException("Not Notification")
        return flow
            .filter { value -> SpirokitLifecycleMessage.getLifecycleMessage(value) != null }
            .map { value ->  SpirokitLifecycleMessage.getLifecycleMessage(value)!! }
    }
}
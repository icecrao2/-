package com.study.presentation.feature.ble.viewmodel

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.study.data.repository.SpiroCalBleRepository
import com.study.data.repository.SpiroKitBleRepository
import com.study.domain.model.Device
import com.study.domain.model.DeviceType
import com.study.domain.model.SpiroKitCommand
import com.study.presentation.feature.ble.toDevice
import kotlinx.coroutines.launch

class HomeViewModel(
    private val spiroCalRepository: SpiroCalBleRepository,
    private val spiroKitRepository: SpiroKitBleRepository
): ViewModel() {
    private val _scannedSpiroCalDevice: MutableLiveData<List<Device>> = MutableLiveData(emptyList())
    val scannedSpiroCalDevice: LiveData<List<Device>> = _scannedSpiroCalDevice

    private val _scannedSpiroKitDevice: MutableLiveData<List<Device>> = MutableLiveData(emptyList())
    val scannedSpiroKitDevice: LiveData<List<Device>> = _scannedSpiroKitDevice

    suspend fun startCalScan() {
        val scannedSpiroCal = spiroCalRepository.scanDevice()
        scannedSpiroCal?.collect { devices ->
            _scannedSpiroCalDevice.value = devices.map { it.toDevice(DeviceType.SpiroCal) }
        }
    }

    suspend fun startKitScan() {
        val scannedSpiroCal = spiroKitRepository.scanDevice()
        scannedSpiroCal?.collect { devices ->
            _scannedSpiroKitDevice.value = devices.map { it.toDevice(DeviceType.SpiroKit) }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectCal(device: Device) {
        if(spiroCalRepository.isConnected) return

        viewModelScope.launch {
            spiroCalRepository.connectDevice(device).collect { data ->
                println("cal data = $data")
            }
        }

        viewModelScope.launch {
            spiroCalRepository.observeBatteryData().collect { data ->
                println("cal battery = $data")
            }
        }
1
        viewModelScope.launch {
            spiroCalRepository.observeEnvironmentData().collect { data ->
                println("cal environment = $data")
            }
        }

        viewModelScope.launch {
            println("spiroCalRepository.connectionStatus = ${spiroCalRepository.connectionStatus}")
            spiroCalRepository.connectionStatus?.collect { connectionStatus ->
                println("spirocal $connectionStatus")
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectKit(device: Device) {
        if(spiroKitRepository.isConnected) return

        viewModelScope.launch {
            spiroKitRepository.connectDevice(device).collect { data ->
                println("spirokit data = $data")
            }
        }

        viewModelScope.launch {
            spiroKitRepository.observeBatteryData().collect { data ->
                println("spirokit battery = $data")
            }
        }

        viewModelScope.launch {
            spiroKitRepository.observeLifecycleMessage().collect { data ->
                println("spirokit lifecycle = $data")
            }
        }

        viewModelScope.launch {
            println("spiroKitRepository.connectionStatus = ${spiroKitRepository.connectionStatus}")
            spiroKitRepository.connectionStatus?.collect { connectionStatus ->
                println("spirokit $connectionStatus")
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun sendSpirokitCommand(command: SpiroKitCommand) {
        spiroKitRepository.sendCommand(command)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnectKit() {
        spiroKitRepository.disconnectDevice()
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnectCal() {
        spiroCalRepository.disconnectDevice()
    }
}
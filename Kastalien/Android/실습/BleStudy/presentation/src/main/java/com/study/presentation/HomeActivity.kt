package com.study.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.domain.model.ReceiveBatteryCommand
import com.study.presentation.databinding.ActivityHomeBinding
import com.study.presentation.feature.ble.adapter.ScannedDeviceListAdapter
import com.study.presentation.feature.ble.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private val REQUEST_BLUETOOTH_PERMISSIONS = 1001
class HomeActivity : AppCompatActivity() {

    private val homeViewModel: HomeViewModel by viewModel()

    private val activityBinding: ActivityHomeBinding by lazy {
        ActivityHomeBinding.inflate(
            LayoutInflater.from(this)
        )
    }
    @SuppressLint("MissingPermission")
    private val scannedSpiroCalDeviceAdapter = ScannedDeviceListAdapter{ device ->
        lifecycleScope.launch {
            homeViewModel.connectCal(device)
        }
    }
    @SuppressLint("MissingPermission")
    private val scannedSpiroKitDeviceAdapter = ScannedDeviceListAdapter{ device ->
        lifecycleScope.launch {
            homeViewModel.connectKit(device)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(activityBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        ensureBlePermissions()
        attachListAdapter()
        observeScannedDevice()
    }
    private fun ensureBlePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val perms = mutableListOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )

            val notGranted = perms.filter {
                ContextCompat.checkSelfPermission(this, it) !=
                        PackageManager.PERMISSION_GRANTED
            }

            if (notGranted.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    notGranted.toTypedArray(),
                    REQUEST_BLUETOOTH_PERMISSIONS
                )
                return
            }
        } else {
            // Android 11 이하에서는 위치 권한 체크
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_BLUETOOTH_PERMISSIONS
                )
                return
            }
        }
    }

    private fun attachListAdapter() {
        activityBinding.scannedSpiroCalList.apply {
            adapter = scannedSpiroCalDeviceAdapter
            layoutManager = LinearLayoutManager(context)
        }

        activityBinding.scannedSpiroKitList.apply {
            adapter = scannedSpiroKitDeviceAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeScannedDevice() {
        homeViewModel.scannedSpiroCalDevice.observe(this) {
            scannedSpiroCalDeviceAdapter.submitList(it)
        }

        homeViewModel.scannedSpiroKitDevice.observe(this) {
            scannedSpiroKitDeviceAdapter.submitList(it)
        }
    }

    override fun onStart() {
        super.onStart()
        setCalScanButtonEvent()
        setKitScanButtonEvent()
        val kitOffButton = findViewById<Button>(R.id.kit_off_button)
        kitOffButton.setOnClickListener {
            lifecycleScope.launch {
//                homeViewModel.sendSpirokitCommand(TurnOffCommand())
                homeViewModel.sendSpirokitCommand(ReceiveBatteryCommand())
            }
        }

        val kitDisconnectButton = findViewById<Button>(R.id.kit_disconnect_button)
        kitDisconnectButton.setOnClickListener {
            lifecycleScope.launch {
                homeViewModel.disconnectKit()
            }
        }

        val calDisconnectButton = findViewById<Button>(R.id.cal_disconnect_button)
        calDisconnectButton.setOnClickListener {
            lifecycleScope.launch {
                homeViewModel.disconnectCal()
            }
        }
    }

    private fun setCalScanButtonEvent() {
        val calScanButton = findViewById<Button>(R.id.cal_scan_button)
        calScanButton.setOnClickListener {
            lifecycleScope.launch {
                homeViewModel.startCalScan()
            }
        }
    }

    private fun setKitScanButtonEvent() {
        val kitScanButton = findViewById<Button>(R.id.kit_scan_button)
        kitScanButton.setOnClickListener {
            lifecycleScope.launch {
                homeViewModel.startKitScan()
            }
        }
    }
}
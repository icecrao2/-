package com.study.presentation.feature.ble.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.study.domain.model.Device
import com.study.presentation.databinding.LayoutScannedDeviceItemBinding

class ScannedDeviceListAdapter(
    private val onClickListener: (Device) -> Unit
): androidx.recyclerview.widget.ListAdapter<Device, ScannedDeviceListAdapter.ScannedDeviceListViewHolder>(
    DIFF_CALLBACK
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ScannedDeviceListViewHolder = ScannedDeviceListViewHolder(
        LayoutScannedDeviceItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ),
        onClickListener
    )

    override fun onBindViewHolder(
        holder: ScannedDeviceListViewHolder,
        position: Int
    ) = holder.bind(getItem(position))


    class ScannedDeviceListViewHolder(
        private val binding: LayoutScannedDeviceItemBinding, private val onClickListener: (Device) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(device: Device) {
            binding.scannedDeviceItem.text = device.name
            binding.scannedDeviceItem.setOnClickListener {
                onClickListener(device)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Device>() {
            override fun areItemsTheSame(oldItem: Device, newItem: Device): Boolean =
                oldItem.address == newItem.address

            override fun areContentsTheSame(oldItem: Device, newItem: Device): Boolean =
                oldItem == newItem
        }
    }
}

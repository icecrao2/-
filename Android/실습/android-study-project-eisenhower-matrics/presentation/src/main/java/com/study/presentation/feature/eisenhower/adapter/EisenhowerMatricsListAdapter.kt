package com.study.presentation.feature.eisenhower.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.study.domain.model.Todo
import com.study.presentation.databinding.LayoutListItemTextBinding

class EisenhowerMatricsListAdapter(private val onClickListener: (Todo) -> Unit) :
    ListAdapter<Todo, EisenhowerMatricsListAdapter.EisenhowerMatricsListViewHolder>(
        DIFF_CALLBACK
    ) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): EisenhowerMatricsListViewHolder = EisenhowerMatricsListViewHolder(
        LayoutListItemTextBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ), onClickListener
    )

    override fun onBindViewHolder(
        holder: EisenhowerMatricsListViewHolder, position: Int
    ) = holder.bind(getItem(position))

    class EisenhowerMatricsListViewHolder(
        private val binding: LayoutListItemTextBinding, private val onClickListener: (Todo) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: Todo) {
            binding.listItemText.text = todo.name
            binding.listItemText.setOnClickListener {
                onClickListener(todo)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean =
                oldItem == newItem
        }
    }
}
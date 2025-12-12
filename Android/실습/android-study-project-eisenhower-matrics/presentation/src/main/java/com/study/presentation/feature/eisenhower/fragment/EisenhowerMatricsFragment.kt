package com.study.presentation.feature.eisenhower.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.study.domain.model.Todo
import com.study.presentation.common.base.BaseFragment
import com.study.presentation.databinding.FragmentEisenhowerMatricsBinding
import com.study.presentation.feature.eisenhower.adapter.EisenhowerMatricsListAdapter
import com.study.presentation.feature.eisenhower.viewmodel.EditTodoFormEvent
import com.study.presentation.feature.eisenhower.viewmodel.EditTodoViewModel
import com.study.presentation.feature.eisenhower.viewmodel.EisenhowerMatricsListEvent
import com.study.presentation.feature.eisenhower.viewmodel.EisenhowerMatricsListViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class EisenhowerMatricsFragment : BaseFragment<FragmentEisenhowerMatricsBinding>(FragmentEisenhowerMatricsBinding::inflate) {
    private val doThisNowListAdapter = EisenhowerMatricsListAdapter { onClickTodoItem(it) }
    private val handItOffListAdapter = EisenhowerMatricsListAdapter {onClickTodoItem(it)}
    private val planForThisListAdapter = EisenhowerMatricsListAdapter {onClickTodoItem(it)}
    private val letItGoListAdapter = EisenhowerMatricsListAdapter {onClickTodoItem(it)}

    private val eisenhowerMatricsListViewModel: EisenhowerMatricsListViewModel by activityViewModel()
    private val editTodoViewModel: EditTodoViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupEisenhowerMatricsRecyclerView()
        observeEisenhowerMatricsTodoList()
        eisenhowerMatricsListViewModel.onEvent(EisenhowerMatricsListEvent.Load())
    }

    private fun setupEisenhowerMatricsRecyclerView() {
        binding.apply {
            doThisNowRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = doThisNowListAdapter
            }
            handThisOffRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = handItOffListAdapter
            }
            planForThisRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = planForThisListAdapter
            }
            letItGoRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = letItGoListAdapter
            }
        }
    }

    private fun observeEisenhowerMatricsTodoList() {
        eisenhowerMatricsListViewModel.uiStatus.observe(viewLifecycleOwner) {
            doThisNowListAdapter.submitList(eisenhowerMatricsListViewModel.urgentImportantTodoList)
            handItOffListAdapter.submitList(eisenhowerMatricsListViewModel.urgentNorImportantTodoList)
            planForThisListAdapter.submitList(eisenhowerMatricsListViewModel.notUrgentImportantTodoList)
            letItGoListAdapter.submitList(eisenhowerMatricsListViewModel.notUrgentNotImportantTodoList)
        }
    }

    private fun onClickTodoItem(todo: Todo) {
        editTodoViewModel.onEvent(EditTodoFormEvent.SelectTodo(todo))
    }

    companion object {
        fun newInstance() = EisenhowerMatricsFragment()
    }
}
package com.study.presentation.feature.eisenhower.fragment

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.study.presentation.common.base.BaseFragment
import com.study.presentation.common.base.UiStatus
import com.study.presentation.common.messages.ToastMessage
import com.study.presentation.databinding.FragmentTodoFormBinding
import com.study.presentation.feature.eisenhower.viewmodel.TodoFormEffect
import com.study.presentation.feature.eisenhower.viewmodel.TodoFormEvent
import com.study.presentation.feature.eisenhower.viewmodel.TodoFormViewModel
import kotlinx.coroutines.launch

abstract class TodoFormFragment(): BaseFragment<FragmentTodoFormBinding>(FragmentTodoFormBinding::inflate) {

    abstract val todoFormViewModel: TodoFormViewModel

    protected abstract fun onSubmit()

    protected open fun onInitForm() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = todoFormViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        onInitForm()
        setOnClickSubmitButton()
        setOnValueChangedListener()
        observeViewModelStatus()
        observeViewModelEffect()
    }

    private fun setOnClickSubmitButton() {
        binding.submitButton.setOnClickListener {
            onSubmit()
        }
    }

    private fun setOnValueChangedListener() {
        setOnNameChangedListener()
        setOnDaysRequiredChangedListener()
        setOnDeadlineChangedListener()
        setOnIsImportantChangedListener()
        setOnDescriptionChangedListener()
    }

    private fun setOnNameChangedListener() {
        binding.nameEditText.addTextChangedListener {
            todoFormViewModel.onEvent(TodoFormEvent.ChangeName(it.toString()))
        }
    }

    private fun setOnDaysRequiredChangedListener() {
        binding.daysRequiredEditText.addTextChangedListener {
            todoFormViewModel.onEvent(TodoFormEvent.ChangeDaysRequired(it.toString()))
        }
    }

    private fun setOnDeadlineChangedListener() {
        binding.deadlineRequiredEditText.addTextChangedListener {
            todoFormViewModel.onEvent(TodoFormEvent.ChangeDeadline(it.toString()))
        }
    }

    private fun setOnIsImportantChangedListener() {
        binding.importantCheckbox.setOnCheckedChangeListener { _, isChecked ->
            todoFormViewModel.onEvent(TodoFormEvent.ChangeIsImportant(isChecked))
        }
    }

    private fun setOnDescriptionChangedListener() {
        binding.descriptionEditText.addTextChangedListener {
            todoFormViewModel.onEvent(TodoFormEvent.ChangeDescription(it.toString()))
        }
    }

    private fun observeViewModelStatus() {
        todoFormViewModel.uiStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is UiStatus.Fail -> Unit
                is UiStatus.Idle -> Unit
                is UiStatus.Success -> Unit
            }
        }
    }

    private fun observeViewModelEffect() {
        viewLifecycleOwner.lifecycleScope.launch {
            todoFormViewModel.effect.collect { effect ->
                when (effect) {
                    is TodoFormEffect.ShowToast -> {
                        ToastMessage.showShortMessage(
                            requireContext(),
                            effect.message
                        )
                    }
                }
            }
        }
    }
}
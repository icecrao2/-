package com.study.presentation.feature.eisenhower

import BaseActivity
import android.os.Bundle
import android.view.View
import com.study.presentation.R
import com.study.presentation.common.base.UiStatus
import com.study.presentation.databinding.ActivityEisenhowerBinding
import com.study.presentation.feature.eisenhower.fragment.AddTodoFragment
import com.study.presentation.feature.eisenhower.fragment.EditTodoFragment
import com.study.presentation.feature.eisenhower.fragment.EisenhowerMatricsFragment
import com.study.presentation.feature.eisenhower.viewmodel.EditTodoFormEvent
import com.study.presentation.feature.eisenhower.viewmodel.EditTodoOperation
import com.study.presentation.feature.eisenhower.viewmodel.EditTodoViewModel
import com.study.presentation.feature.eisenhower.viewmodel.TodoFormOperation
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue


class EisenhowerActivity() :
    BaseActivity<ActivityEisenhowerBinding>(ActivityEisenhowerBinding::inflate) {
    private val editTodoViewModel: EditTodoViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addFragments()
        setOnClickOpenAddTodoFragmentButton()
        observeTodoIsSelected()
        observeEditTodoViewModelStatus()
    }

    private fun addFragments() {
        supportFragmentManager.beginTransaction()
            .add(R.id.eisenhower_matrics_list_fragment, EisenhowerMatricsFragment.newInstance())
            .add(R.id.eisenhower_matrics_todo_form_fragment, AddTodoFragment.newInstance())
            .commit()
    }

    private fun setOnClickOpenAddTodoFragmentButton() {
        activityBinding.openAddTodoButton.setOnClickListener {
            when (activityBinding.openAddTodoButton.text) {
                "+" -> onClickOpenAddTodoButton()
                "-" -> onClickCloseAddTodoButton()
            }
        }
    }

    private fun onClickOpenAddTodoButton() {
        attachAddTodoFragmentToFormContainer()
    }

    private fun attachAddTodoFragmentToFormContainer() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.eisenhower_matrics_todo_form_fragment, AddTodoFragment.newInstance())
            .commitNow()

        activityBinding.eisenhowerMatricsTodoFormFragment.visibility = View.VISIBLE
        activityBinding.openAddTodoButton.text = "-"
    }

    private fun onClickCloseAddTodoButton() {
        val todoFormFragment =
            supportFragmentManager.findFragmentById(R.id.eisenhower_matrics_todo_form_fragment)
        when (todoFormFragment) {
            is AddTodoFragment -> detachAddTodoFragmentFormFormContainer()
            is EditTodoFragment -> detachEditTodoFragmentFromFormContainer()
        }
    }

    private fun detachAddTodoFragmentFormFormContainer() {
        activityBinding.eisenhowerMatricsTodoFormFragment.visibility = View.GONE
        activityBinding.openAddTodoButton.text = "+"
    }

    private fun detachEditTodoFragmentFromFormContainer() {
        editTodoViewModel.onEvent(EditTodoFormEvent.DeselectTodo())

        activityBinding.apply {
            eisenhowerMatricsTodoFormFragment.visibility = View.GONE
            openAddTodoButton.text = "+"
        }
    }

    private fun observeTodoIsSelected() {
        editTodoViewModel.isSelected.observe(this) {
            when(it) {
                true -> attachEditTodoFragmentToFormContainer()
                else -> detachEditTodoFragmentFromFormContainer()
            }
        }
    }

    private fun attachEditTodoFragmentToFormContainer() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.eisenhower_matrics_todo_form_fragment, EditTodoFragment.newInstance())
            .commitNow()

        activityBinding.apply {
            eisenhowerMatricsTodoFormFragment.visibility = View.VISIBLE
            openAddTodoButton.text = "-"
        }
    }

    private fun observeEditTodoViewModelStatus() {
        editTodoViewModel.uiStatus.observe(this) {
            when (it) {
                is UiStatus.Success -> when (it.operation) {
                    is TodoFormOperation.Submit -> editTodoViewModel.onEvent(EditTodoFormEvent.DeselectTodo())
                    is EditTodoOperation.Remove -> editTodoViewModel.onEvent(EditTodoFormEvent.DeselectTodo())
                    else -> Unit
                }
                else -> Unit
            }
        }
    }
}
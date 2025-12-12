package com.study.presentation.feature.eisenhower.fragment

import com.study.presentation.R
import com.study.presentation.feature.eisenhower.viewmodel.AddTodoViewModel
import com.study.presentation.feature.eisenhower.viewmodel.TodoFormEvent
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class AddTodoFragment : TodoFormFragment() {
    override val todoFormViewModel: AddTodoViewModel by activityViewModel()

    override fun onSubmit() {
        todoFormViewModel.onEvent(TodoFormEvent.Submit())
    }

    companion object {
        fun newInstance() = AddTodoFragment()
    }
}
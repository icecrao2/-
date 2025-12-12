package com.study.presentation.feature.eisenhower.fragment

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import com.study.presentation.databinding.LayoutButtonBinding
import com.study.presentation.feature.eisenhower.viewmodel.EditTodoFormEvent
import com.study.presentation.feature.eisenhower.viewmodel.EditTodoViewModel
import com.study.presentation.feature.eisenhower.viewmodel.TodoFormEvent
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class EditTodoFragment : TodoFormFragment() {

    override val todoFormViewModel: EditTodoViewModel by activityViewModel()


    override fun onInitForm() {
        super.onInitForm()
        attachRemoveButton()
    }

    private fun attachRemoveButton() {
        binding.layoutRoot.addView(getRemoveButtonBinding())
    }

    private fun getRemoveButtonBinding(): Button {
        return LayoutButtonBinding.inflate(
            LayoutInflater.from(context), binding.layoutRoot, false
        ).root.apply {
            text = "remove"
            setBackgroundColor(Color.RED)
            setTextColor(Color.WHITE)
            setOnClickListener { removeTodo() }
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT, TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics
                ).toInt()
            ).apply {
                bottomToTop = binding.submitButton.id
            }
        }
    }

    override fun onSubmit() {
        todoFormViewModel.onEvent(TodoFormEvent.Submit())
    }

    private fun removeTodo() {
        todoFormViewModel.onEvent(EditTodoFormEvent.RemoveTodo())
    }

    companion object {
        fun newInstance() = EditTodoFragment()
    }
}
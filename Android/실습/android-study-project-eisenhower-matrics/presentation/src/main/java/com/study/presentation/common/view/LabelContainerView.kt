package com.study.presentation.common.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.study.presentation.R
import com.study.presentation.databinding.LayoutLabelBinding

class LabelContainerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val labelBinding: LayoutLabelBinding

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.LabelContainerView)
        val labelText = a.getString(R.styleable.LabelContainerView_labelText) ?: ""
        a.recycle()

        labelBinding = LayoutLabelBinding.inflate(
            LayoutInflater.from(context),
            this,
            false
        ).apply {
            root.id = generateViewId()
            root.text = labelText
            root.layoutParams = LayoutParams(
                0,
                LayoutParams.MATCH_PARENT
            ).apply {
                matchConstraintDefaultWidth = LayoutParams.MATCH_CONSTRAINT_PERCENT
                matchConstraintPercentWidth = 0.2f
                startToStart = LayoutParams.PARENT_ID
                topToTop = LayoutParams.PARENT_ID
                bottomToBottom = LayoutParams.PARENT_ID
            }
        }

        addView(labelBinding.root)
    }

    override fun onViewAdded(child: View) {
        super.onViewAdded(child)
        if (child === labelBinding.root) return

        val layoutParams = child.layoutParams as? LayoutParams ?: return

        layoutParams.apply {
            width = 0
            startToEnd = labelBinding.root.id
            endToEnd = LayoutParams.PARENT_ID
        }

        child.layoutParams = layoutParams
    }
}
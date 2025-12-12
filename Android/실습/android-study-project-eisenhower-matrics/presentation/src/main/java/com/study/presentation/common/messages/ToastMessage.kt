package com.study.presentation.common.messages

import android.content.Context
import android.widget.Toast
import android.widget.Toast.makeText

class ToastMessage {
    companion object {
        fun showShortMessage(
            context: Context, message: String
        ) {
            makeText(
                context, message, Toast.LENGTH_SHORT
            ).show()
        }
    }
}


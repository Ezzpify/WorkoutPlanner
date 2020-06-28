package com.casper.workouts.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.Window
import com.casper.workouts.R
import com.casper.workouts.callbacks.InputDialogCallback
import kotlinx.android.synthetic.main.dialog_error.dialog_description
import kotlinx.android.synthetic.main.dialog_error.dialog_title
import kotlinx.android.synthetic.main.dialog_input.*

class InputDialog(context: Context,
                private val title: String,
                private val desc: String,
                private val currentValue: String,
                private val inputType: Int,
                private val inputDialogCallback: InputDialogCallback) : Dialog(context) {

    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_input)

        dialog_title.text = title
        dialog_description.text = desc

        input_value.setText(currentValue)
        input_value.inputType = inputType

        button_cancel.setOnClickListener {
            dismiss()
        }

        button_ok.setOnClickListener {
            val newValue = input_value.text.toString().trim()

            // No point making a callback if value didn't change
            // I'm calling it now that in 2 years time this piece of code will cause me hours of headache.
            if (newValue != currentValue) {
                inputDialogCallback.result(newValue)
            }

            dismiss()
        }
    }
}
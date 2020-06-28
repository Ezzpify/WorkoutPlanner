package com.casper.workouts.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.casper.workouts.R
import com.casper.workouts.callbacks.OptionDialogCallback
import kotlinx.android.synthetic.main.dialog_error.*
import kotlinx.android.synthetic.main.dialog_error.dialog_description
import kotlinx.android.synthetic.main.dialog_error.dialog_title
import kotlinx.android.synthetic.main.dialog_option.*

class OptionDialog(context: Context,
                private val title: String,
                private val desc: String,
                private val buttonOneText: String,
                private val buttonTwoText: String,
                private val optionDialogCallback: OptionDialogCallback) : Dialog(context) {
    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_option)

        dialog_title.text = title
        dialog_description.text = desc

        button_one.text = buttonOneText
        button_one.setOnClickListener {
            optionDialogCallback.optionOneClicked()
            dismiss()
        }

        button_two.text = buttonTwoText
        button_two.setOnClickListener {
            optionDialogCallback.optionTwoClicked()
            dismiss()
        }
    }
}
package com.example.inktestapp.utils

import android.app.Activity
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import com.example.inktestapp.R

object DialogHelper {

    fun showSaveNoteDialog(
        title: String,
        activity: Activity?,
        onSaveClicked: (title: String) -> Unit
    ) {
        activity?.let {
            if (!it.isFinishing && !it.isDestroyed) {
                val builder = AlertDialog.Builder(activity)
                val customView = View.inflate(activity, R.layout.save_note_layout, null)
                val btnCancel = customView.findViewById<AppCompatButton>(R.id.btn_save_note_cancel)
                val btnSave = customView.findViewById<AppCompatButton>(R.id.btn_save_note)
                val saveNoteEditText = customView.findViewById<EditText>(R.id.et_save_note)

                builder.setView(customView)
                val dialog = builder.create()
                dialog.setCancelable(false)
                dialog.show()

                if (title.isNotBlank()) {
                    saveNoteEditText.setText(title)
                }

                btnCancel.setOnClickListener {
                    dialog.cancel()
                }
                btnSave.setOnClickListener {
                    val text = saveNoteEditText.text.toString().trim()
                    if (text.isNotEmpty()) {
                        dialog.cancel()
                        onSaveClicked(text)
                    }
                }
            }
        }
    }

    fun showAlertDialogTwoActions(
        activity: Activity?,
        title: String,
        message: String,
        negativeBtnText: String = "Cancel",
        positiveBtnText: String = "Ok",
        onOkClicked: () -> Unit,
        onCancelClicked: () -> Unit
    ) {
        activity?.let {
            if (!it.isFinishing && !it.isDestroyed) {
                val dialog = AlertDialog.Builder(activity)
                dialog.setTitle(title)
                    .setMessage(message)
                    .setNegativeButton(negativeBtnText) { _, _ -> onCancelClicked() }
                    .setPositiveButton(positiveBtnText) { _, _ -> onOkClicked() }
                dialog.show()
            }
        }
    }
}
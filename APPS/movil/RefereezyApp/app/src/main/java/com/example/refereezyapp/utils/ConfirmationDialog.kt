package com.example.refereezyapp.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.refereezyapp.R


class ConfirmationDialog {
    companion object {
        fun showReportDialog(
            context: Context,
            title: String,
            message: String,
            onConfirm: () -> Unit,
            onCancel: () -> Unit
        ) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.layout_confirmation_dialog, null)
            val dialog = AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(false)
                .create()

            val titleText = dialogView.findViewById<TextView>(R.id.dialog_title)
            val messageText = dialogView.findViewById<TextView>(R.id.dialog_message)
            val confirmButton = dialogView.findViewById<Button>(R.id.dialog_confirm)
            val cancelButton = dialogView.findViewById<Button>(R.id.dialog_cancel)

            titleText.text = title
            messageText.text = message

            confirmButton.setOnClickListener {
                onConfirm()
                dialog.dismiss()
            }

            cancelButton.setOnClickListener {
                onCancel()
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}
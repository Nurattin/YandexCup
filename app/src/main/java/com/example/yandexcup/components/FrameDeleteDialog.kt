package com.example.yandexcup.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@Composable
fun FrameDeleteDialog(
    onDeleteAllFrames: () -> Unit,
    onDeleteCurrentFrame: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Удаление кадров") },
        text = { Text("Выберите действие:") },
        confirmButton = {
            TextButton(onClick = {
                onDeleteAllFrames()
                onDismiss()
            }) {
                Text("Удалить все кадры")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDeleteCurrentFrame()
                onDismiss()
            }) {
                Text("Удалить этот кадр")
            }
        },
        properties = DialogProperties(dismissOnClickOutside = true)
    )
}

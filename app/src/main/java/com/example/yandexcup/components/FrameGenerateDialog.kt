package com.example.yandexcup.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun FrameGenerateDialog(
    visible: Boolean,
    onGenerateFrames: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    if (visible) {
        var frameCount by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Генерация Кадров") },
            text = {
                Column {
                    Text("Введите количество кадров для генерации:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = frameCount,
                        onValueChange = {
                            frameCount = it
                            isError = it.toIntOrNull() == null && it.isNotEmpty()
                        },
                        label = { Text("Количество (N)") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        isError = isError,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (isError) {
                        Text(
                            text = "Пожалуйста, введите корректное целое число.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val n = frameCount.toIntOrNull()
                        if (n != null && n > 0) {
                            onGenerateFrames(n)
                            onDismiss()
                        } else {
                            isError = true
                        }
                    }
                ) {
                    Text("Генерировать")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Отмена")
                }
            },
        )
    }
}

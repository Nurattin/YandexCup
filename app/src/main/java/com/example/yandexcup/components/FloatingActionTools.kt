package com.example.yandexcup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.yandexcup.R

class FloatingActionTools {
}

@Composable
fun FloatingActionTools(
    modifier: Modifier = Modifier,
    onEraseClick: () -> Unit,
    isEraseMode: Boolean,
) {
    Column(
        modifier = modifier,

        ) {
        IconButton(
            onClick = onEraseClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = if (isEraseMode) Color.Red else Color.Black,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_erase),
                contentDescription = null,
                tint = Color.White,
            )
        }
    }
}
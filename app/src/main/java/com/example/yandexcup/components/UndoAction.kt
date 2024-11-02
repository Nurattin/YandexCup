package com.example.yandexcup.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yandexcup.R
import com.example.yandexcup.ui.theme.YandexCupTheme

@Composable
fun UndoAction(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .width(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                onClick = onClick,
                enabled = enabled,
            ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.ic_undo),
            contentDescription = null,
            tint = Color.White,
        )
        Text(
            text = "ОТМЕНИТЬ",
            modifier = Modifier
                .fillMaxWidth(),
            fontSize = 10.sp,
            maxLines = 1,
            color = Color.White,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun RedoAction(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .width(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                onClick = onClick,
                enabled = enabled,
            ),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.graphicsLayer {
                scaleX = -1f
            },
            imageVector = ImageVector.vectorResource(R.drawable.ic_undo),
            contentDescription = null,
            tint = Color.White,
        )
        Text(
            text = "ВЕРНУТЬ",
            modifier = Modifier
                .fillMaxWidth(),
            fontSize = 10.sp,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview
@Composable
private fun PreviewRedoAction() {
    YandexCupTheme {
        RedoAction(
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun PreviewMoveAction() {
    YandexCupTheme {
        UndoAction(
            onClick = {},
        )
    }
}
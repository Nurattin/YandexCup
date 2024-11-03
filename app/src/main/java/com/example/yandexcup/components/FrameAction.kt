package com.example.yandexcup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.yandexcup.R
import com.example.yandexcup.ui.theme.YandexCupTheme


@Composable
fun FrameAction(
    show: Boolean,
    offset: IntOffset = IntOffset(0, 0),
    onDeleteClick: () -> Unit,
    onCopyClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    if (show) {
        Popup(
            offset = offset,
            onDismissRequest = onDismissRequest
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FrameActionTab(
                    onClick = onDeleteClick,
                    icon = ImageVector.vectorResource(R.drawable.ic_round_delete),
                    text = "УДАЛИТЬ"
                )
                FrameActionTab(
                    onClick = onCopyClick,
                    icon = ImageVector.vectorResource(R.drawable.ic_round_content_copy),
                    text = "ДУБЛИРОВАТЬ"
                )
            }
        }
    }
}

@Composable
fun FrameActionTab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector,
    text: String,
) {
    Column(
        modifier = modifier
            .width(50.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable(
                onClick = onClick,
            ),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
        )
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = Color.White,
        )
    }
}

@Preview
@Composable
private fun PreviewFrameAction() {
    YandexCupTheme {
        FrameAction(
            onDeleteClick = {},
            onCopyClick = {},
            onDismissRequest = {},
            show = true
        )
    }
}

@Preview
@Composable
private fun PreviewFrameActionTab() {
    YandexCupTheme {
        FrameActionTab(
            onClick = {},
            icon = ImageVector.vectorResource(R.drawable.ic_round_delete),
            text = "Delete"
        )
    }
}
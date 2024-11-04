package com.example.yandexcup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.yandexcup.ui.theme.YandexCupTheme

@Composable
fun AddNewFrame(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp.dp
    val screenHeight = config.screenHeightDp.dp
    Box(
        modifier = modifier
            .size(
                width = screenWidth * 0.15f,
                height = screenHeight * 0.1f
            )
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFEFEFEF))
            .dashedBorder(color = Color.Gray)
            .clickable(
                onClick = onClick,
            )
    ) {
        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .size(32.dp),
            contentDescription = null,
            imageVector = Icons.Rounded.Add,
            tint = Color.Black,
        )
    }
}

private fun Modifier.dashedBorder(
    color: Color,
    width: Dp = 2.dp,
    dashWidth: Float = 20f,
    dashGap: Float = 20f,
) = this.then(
    Modifier.drawBehind {
        val stroke = Stroke(
            width = width.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth, dashGap))
        )

        drawRoundRect(
            color = color,
            style = stroke,
            cornerRadius = CornerRadius(
                12.dp.toPx(),
                12.dp.toPx()
            )
        )
    }
)

@Preview
@Composable
private fun PreviewAddNewFrame() {
    YandexCupTheme {
        AddNewFrame(
            onClick = {}
        )
    }
}
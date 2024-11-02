package com.example.yandexcup.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yandexcup.coreUi.PathProperties
import com.example.yandexcup.ui.theme.YandexCupTheme


@Composable
fun FrameTab(
    modifier: Modifier = Modifier,
    selected: Boolean,
    number: Int,
    path: List<Pair<Path, PathProperties>>,
    onClick: () -> Unit,
) {
    val scaleH = 0.1f
    val scaleW = 0.15f

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
            .border(
                shape = RoundedCornerShape(12.dp),
                color = if (selected) Color.Red else Color.Gray,
                width = 1.dp
            )
            .background(Color.White)
            .clickable(
                onClick = onClick,
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            with(drawContext.canvas.nativeCanvas) {
                val checkPoint = saveLayer(null, null)
                scale(scaleW, scaleH)

                path.forEach { (path, property) ->
                    if (!property.eraseMode) {
                        drawPath(
                            color = property.color,
                            path = path,
                            style = Stroke(
                                width = property.strokeWidth * scaleW,
                                cap = property.strokeCap,
                                join = property.strokeJoin
                            )
                        )
                    } else {
                        drawPath(
                            color = Color.Transparent,
                            path = path,
                            style = Stroke(
                                width = property.strokeWidth * scaleH,
                                cap = property.strokeCap,
                                join = property.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }
                restoreToCount(checkPoint)
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    if (selected) Color.Red else Color.Transparent
                )
                .padding(horizontal = 2.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = number.toString(),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = if (selected) Color.White else Color.Black,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewFrameTab() {
    YandexCupTheme {
        FrameTab(
            selected = false,
            onClick = {},
            number = 1,
            path = emptyList()
        )
    }
}

@Preview
@Composable
private fun PreviewSelectedFrameTab() {
    YandexCupTheme {
        FrameTab(
            selected = true,
            onClick = {},
            number = 2,
            path = emptyList()
        )
    }
}
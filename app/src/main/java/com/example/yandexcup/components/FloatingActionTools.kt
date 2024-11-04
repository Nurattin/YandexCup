package com.example.yandexcup.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yandexcup.R
import com.example.yandexcup.coreUi.PathProperties
import com.example.yandexcup.ui.theme.YandexCupTheme


@Composable
fun FloatingActionTools(
    modifier: Modifier = Modifier,
    properties: PathProperties,
    toolMode: ActionToolMode,
    onToolClick: (ActionToolMode) -> Unit,
    onColorPickerClick: () -> Unit,
    onPathPickerClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .animateContentSize()
            .width(50.dp)
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ActionTool(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            selected = toolMode == ActionToolMode.Erase,
            icon = ImageVector.vectorResource(R.drawable.ic_erase),
            onClick = {
                onToolClick(ActionToolMode.Erase)
            }
        )
        ActionTool(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            selected = toolMode == ActionToolMode.Pencil,
            icon = ImageVector.vectorResource(R.drawable.ic_pencil__edit__create),
            onClick = {
                onToolClick(ActionToolMode.Pencil)
            }
        )
        HorizontalDivider(
            color = Color.LightGray,
        )
        if (toolMode == ActionToolMode.Pencil) {

            ActionColorPicker(
                modifier = Modifier
                    .fillMaxWidth(),
                color = properties.color,
                onClick = onColorPickerClick,
            )
        }

        if (toolMode == ActionToolMode.Pencil || toolMode == ActionToolMode.Erase) {
            ActionPathSizePicker(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = onPathPickerClick,
                strokeWidth = properties.strokeWidth,
            )
        }
    }
}

@Composable
private fun ActionTool(
    modifier: Modifier = Modifier,
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    val color by animateColorAsState(
        if (selected) Color.Red else Color.Transparent
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .clickable(
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
        )
    }
}

@Composable
private fun ActionColorPicker(
    modifier: Modifier = Modifier,
    color: Color,
    onClick: () -> Unit,
) {
    val animatedColor by animateColorAsState(color)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(
                onClick = onClick,
            ),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = Color.White,
                    shape = RoundedCornerShape(6.dp)
                )
                .size(32.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(animatedColor),
        )
        Text(
            text = "ЦВЕТ",
            color = Color.White,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ActionPathSizePicker(
    modifier: Modifier = Modifier,
    strokeWidth: Float,
    onClick: () -> Unit,
) {
    val strokeWidthDp = ((strokeWidth / MAX_PATH_STROKE_WIDTH) * 32).coerceIn(1f, 32f).dp

    Column(
        modifier = modifier
            .clickable(
                onClick = onClick,
            ),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(strokeWidthDp)
                    .clip(CircleShape)
                    .background(Color.White),
            )
        }
        Text(
            text = "РАЗМЕР",
            color = Color.White,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun PreviewActionTool() {
    YandexCupTheme {
        ActionTool(
            selected = true,
            icon = ImageVector.vectorResource(R.drawable.ic_erase),
            onClick = {}
        )
    }
}

enum class ActionToolMode {
    Erase, Pencil, ColorPicker, None
}
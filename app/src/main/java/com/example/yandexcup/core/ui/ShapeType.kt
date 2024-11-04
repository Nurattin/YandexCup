package com.example.yandexcup.core.ui

import androidx.annotation.DrawableRes
import androidx.compose.ui.geometry.Offset
import com.example.yandexcup.R

enum class ShapeType(
    @DrawableRes val iconRes: Int,
) {
    None(-1),
    Rectangle(R.drawable.ic_square),
    Circle(R.drawable.ic_circle),
    Triangle(R.drawable.ic_triangle),
    Arrow(R.drawable.ic_arrow_up),
}

sealed interface DrawableShape

data class RectDrawableShape(
    val topLeft: Offset,
    val width: Float,
    val height: Float
) : DrawableShape

data class CircleDrawableShape(
    val center: Offset,
    val radius: Float
) : DrawableShape

data class TriangleDrawableShape(
    val point1: Offset,
    val point2: Offset,
    val point3: Offset
) : DrawableShape

data class ArrowDrawableShape(
    val start: Offset,
    val end: Offset,
    val arrowHeadAngle: Float = 45f,
    val arrowHeadLength: Float = 20f
) : DrawableShape
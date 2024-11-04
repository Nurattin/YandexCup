package com.example.yandexcup.core.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntSize

enum class SnapEdge {
    LEFT,
    RIGHT,
    TOP,
    BOTTOM,
    NONE;

    companion object {
        suspend fun snapToNearestEdge(
            offsetX: Animatable<Float, *>,
            offsetY: Animatable<Float, *>,
            parentSize: IntSize,
            componentSize: IntSize,
            animationSpec: AnimationSpec<Float> = tween(durationMillis = 300)
        ): SnapEdge {
            val distanceLeft = offsetX.value
            val distanceRight = parentSize.width - (offsetX.value + componentSize.width)
            val distanceTop = offsetY.value
            val distanceBottom = parentSize.height - (offsetY.value + componentSize.height)

            val minHorizontal = minOf(distanceLeft, distanceRight)
            val minVertical = minOf(distanceTop, distanceBottom)

            return if (minHorizontal < minVertical) {
                if (distanceLeft < distanceRight) {
                    offsetX.animateTo(
                        targetValue = 0f,
                        animationSpec = animationSpec,
                    )
                    LEFT
                } else {
                    val targetValue = (parentSize.width - componentSize.width).toFloat()
                    offsetX.animateTo(
                        targetValue = targetValue,
                        animationSpec = animationSpec
                    )
                    RIGHT
                }
            } else {
                if (distanceTop < distanceBottom) {
                    offsetY.animateTo(
                        targetValue = 0f,
                        animationSpec = animationSpec,
                    )
                    TOP
                } else {
                    val targetValue = (parentSize.height - componentSize.height).toFloat()
                    offsetY.animateTo(
                        targetValue = targetValue,
                        animationSpec = animationSpec,
                    )
                    BOTTOM
                }
            }
        }
    }
}
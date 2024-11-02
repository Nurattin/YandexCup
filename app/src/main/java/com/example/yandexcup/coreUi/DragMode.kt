package com.example.yandexcup.coreUi

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize


suspend fun AwaitPointerEventScope.awaitDragMotionEvent(
    onTouchEvent: (MotionEvent, PointerInputChange) -> Unit
) {
    val down: PointerInputChange = awaitFirstDown()
    onTouchEvent(MotionEvent.Down, down)

    var pointer = down

    val change: PointerInputChange? =
        awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
            change.consumePositionChange()
        }

    if (change != null) {
        drag(change.id) { pointerInputChange: PointerInputChange ->
            pointer = pointerInputChange
            onTouchEvent(MotionEvent.Move, pointer)
        }

        onTouchEvent(MotionEvent.Up, pointer)
    } else {
        onTouchEvent(MotionEvent.Up, pointer)
    }
}

fun Modifier.dragMotionEvent(onTouchEvent: (MotionEvent, PointerInputChange) -> Unit) = this.then(
    Modifier.pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                awaitDragMotionEvent(onTouchEvent)
            }
        }
    }
)


suspend fun AwaitPointerEventScope.awaitDragMotionEvent(
    onDragStart: (PointerInputChange) -> Unit = {},
    onDrag: (PointerInputChange) -> Unit = {},
    onDragEnd: (PointerInputChange) -> Unit = {}
) {
    val down: PointerInputChange = awaitFirstDown()
    onDragStart(down)

    var pointer = down

    val change: PointerInputChange? =
        awaitTouchSlopOrCancellation(down.id) { change: PointerInputChange, over: Offset ->
            change.consumePositionChange()
        }

    if (change != null) {
        drag(change.id) { pointerInputChange: PointerInputChange ->
            pointer = pointerInputChange
            onDrag(pointer)
        }

        onDragEnd(pointer)
    } else {
        onDragEnd(pointer)
    }
}

fun Modifier.dragMotionEvent(
    onDragStart: (PointerInputChange) -> Unit = {},
    onDrag: (PointerInputChange) -> Unit = {},
    onDragEnd: (PointerInputChange) -> Unit = {}
) = this.then(
    Modifier.pointerInput(Unit) {
        forEachGesture {
            awaitPointerEventScope {
                awaitDragMotionEvent(onDragStart, onDrag, onDragEnd)
            }
        }
    }
)


enum class DrawMode {
    Draw, Touch, Erase
}


suspend fun snapToNearestEdge(
    offsetX: Animatable<Float, *>,
    offsetY: Animatable<Float, *>,
    parentSize: IntSize,
    componentSize: IntSize
): SnapEdge {
    val distanceLeft = offsetX.value
    val distanceRight = parentSize.width - (offsetX.value + componentSize.width)
    val distanceTop = offsetY.value
    val distanceBottom = parentSize.height - (offsetY.value + componentSize.height)

    val minHorizontal = minOf(distanceLeft, distanceRight)
    val minVertical = minOf(distanceTop, distanceBottom)

    return if (minHorizontal < minVertical) {
        if (distanceLeft < distanceRight) {
            offsetX.animateTo(0f, animationSpec = tween(durationMillis = 300))
            SnapEdge.LEFT
        } else {
            offsetX.animateTo(
                (parentSize.width - componentSize.width).toFloat(),
                animationSpec = tween(durationMillis = 300)
            )
            SnapEdge.RIGHT
        }
    } else {
        if (distanceTop < distanceBottom) {
            offsetY.animateTo(0f, animationSpec = tween(durationMillis = 300))
            SnapEdge.TOP
        } else {
            offsetY.animateTo(
                (parentSize.height - componentSize.height).toFloat(),
                animationSpec = tween(durationMillis = 300)
            )
            SnapEdge.BOTTOM
        }
    }
}

enum class SnapEdge {
    LEFT, RIGHT, TOP, BOTTOM, NONE
}

data class CornerRadii(
    val topStart: Dp,
    val topEnd: Dp,
    val bottomStart: Dp,
    val bottomEnd: Dp
)
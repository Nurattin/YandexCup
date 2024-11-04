package com.example.yandexcup.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.positionChange
import com.example.yandexcup.core.ui.DrawMode
import com.example.yandexcup.core.ui.MotionEvent
import com.example.yandexcup.core.ui.PathProperties
import com.example.yandexcup.core.ui.dragMotionEvent
import kotlin.collections.forEach


@Composable
fun MovieCanvas(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    paths: SnapshotStateList<SnapshotStateList<Pair<Path, PathProperties>>>,
    pathsUndone: SnapshotStateList<SnapshotStateList<Pair<Path, PathProperties>>>,
    isAnimate: Boolean,
    currentPathProperty: PathProperties,
    onCurrentPathPropertyChange: (PathProperties) -> Unit
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        userScrollEnabled = false,
    ) { page ->
        val path = paths[page]
        val previewPath = paths.getOrNull(page - 1)

        var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }

        var currentPosition by remember { mutableStateOf(Offset.Unspecified) }

        var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

        var drawMode by remember { mutableStateOf(DrawMode.Draw) }

        var currentPath by remember { mutableStateOf(Path()) }

        val drawModifier = Modifier
            .fillMaxSize()
            .dragMotionEvent(
                onDragStart = { pointerInputChange ->
                    motionEvent = MotionEvent.Down
                    currentPosition = pointerInputChange.position
                    if (pointerInputChange.pressed != pointerInputChange.previousPressed) {
                        pointerInputChange.consume()
                    }

                },
                onDrag = { pointerInputChange ->
                    motionEvent = MotionEvent.Move
                    currentPosition = pointerInputChange.position

                    if (drawMode == DrawMode.Touch) {
                        val change = pointerInputChange.positionChange()
                        path.forEach { entry ->
                            val path: Path = entry.first
                            path.translate(change)
                        }
                        currentPath.translate(change)
                    }
                    if (pointerInputChange.pressed != pointerInputChange.previousPressed) {
                        pointerInputChange.consume()
                    }

                },
                onDragEnd = { pointerInputChange ->
                    motionEvent = MotionEvent.Up
                    if (pointerInputChange.pressed != pointerInputChange.previousPressed) {
                        pointerInputChange.consume()
                    }
                }
            )

        Canvas(modifier = drawModifier) {

            when (motionEvent) {

                MotionEvent.Down -> {
                    if (drawMode != DrawMode.Touch) {
                        currentPath.moveTo(currentPosition.x, currentPosition.y)
                    }

                    previousPosition = currentPosition

                }

                MotionEvent.Move -> {

                    if (drawMode != DrawMode.Touch) {
                        currentPath.quadraticTo(
                            previousPosition.x,
                            previousPosition.y,
                            (previousPosition.x + currentPosition.x) / 2,
                            (previousPosition.y + currentPosition.y) / 2

                        )
                    }

                    previousPosition = currentPosition
                }

                MotionEvent.Up -> {
                    if (drawMode != DrawMode.Touch) {
                        currentPath.lineTo(currentPosition.x, currentPosition.y)

                        path.add(Pair(currentPath, currentPathProperty))

                        currentPath = Path()

                        onCurrentPathPropertyChange(
                            PathProperties(
                                strokeWidth = currentPathProperty.strokeWidth,
                                color = currentPathProperty.color,
                                strokeCap = currentPathProperty.strokeCap,
                                strokeJoin = currentPathProperty.strokeJoin,
                                eraseMode = currentPathProperty.eraseMode
                            )
                        )
                    }

                    pathsUndone[pagerState.currentPage].clear()

                    currentPosition = Offset.Unspecified
                    previousPosition = currentPosition
                    motionEvent = MotionEvent.Idle
                }

                else -> Unit
            }

            with(drawContext.canvas.nativeCanvas) {

                val checkPoint = saveLayer(null, null)
                if (!isAnimate) {
                    previewPath?.forEach {
                        val path = it.first
                        val property = it.second

                        if (!property.eraseMode) {
                            drawPath(
                                color = property.color
                                    .copy(alpha = .3f),
                                path = path,
                                style = Stroke(
                                    width = property.strokeWidth,
                                    cap = property.strokeCap,
                                    join = property.strokeJoin
                                )
                            )
                        } else {
                            drawPath(
                                color = Color.Transparent,
                                path = path,
                                style = Stroke(
                                    width = property.strokeWidth,
                                    cap = property.strokeCap,
                                    join = property.strokeJoin,
                                ),
                                blendMode = BlendMode.Clear,
                            )
                        }
                    }
                }
                path.forEach {

                    val path = it.first
                    val property = it.second

                    if (!property.eraseMode) {
                        drawPath(
                            color = property.color,
                            path = path,
                            style = Stroke(
                                width = property.strokeWidth,
                                cap = property.strokeCap,
                                join = property.strokeJoin
                            )
                        )
                    } else {
                        drawPath(
                            color = Color.Transparent,
                            path = path,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }

                if (motionEvent != MotionEvent.Idle) {

                    if (!currentPathProperty.eraseMode) {
                        drawPath(
                            color = currentPathProperty.color,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            )
                        )
                    } else {
                        drawPath(
                            color = Color.Transparent,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathProperty.strokeWidth,
                                cap = currentPathProperty.strokeCap,
                                join = currentPathProperty.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }
                restoreToCount(checkPoint)
            }
        }
    }
}
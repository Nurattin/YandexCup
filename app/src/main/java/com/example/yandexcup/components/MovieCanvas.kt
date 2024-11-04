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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.positionChange
import com.example.yandexcup.core.ui.ArrowDrawableShape
import com.example.yandexcup.core.ui.CircleDrawableShape
import com.example.yandexcup.core.ui.DrawMode
import com.example.yandexcup.core.ui.DrawableShape
import com.example.yandexcup.core.ui.MotionEvent
import com.example.yandexcup.core.ui.PathProperties
import com.example.yandexcup.core.ui.RectDrawableShape
import com.example.yandexcup.core.ui.ShapeType
import com.example.yandexcup.core.ui.TriangleDrawableShape
import com.example.yandexcup.core.ui.dragMotionEvent
import kotlin.collections.forEach
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun MovieCanvas(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    paths: SnapshotStateList<SnapshotStateList<Pair<Path, PathProperties>>>,
    pathsUndone: SnapshotStateList<SnapshotStateList<Pair<Path, PathProperties>>>,
    isAnimate: Boolean,
    currentPathProperty: () -> PathProperties,
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
        var shapePosition by remember { mutableStateOf<Offset?>(null) }

        var previousPosition by remember { mutableStateOf(Offset.Unspecified) }

        var drawMode by remember { mutableStateOf(DrawMode.Draw) }
        var currentPath by remember { mutableStateOf(Path()) }
        var previewDrawableShape: DrawableShape? by remember {
            mutableStateOf<DrawableShape?>(null)
        }

        val drawModifier = Modifier
            .fillMaxSize()
            .dragMotionEvent(
                onDragStart = { pointerInputChange ->
                    motionEvent = MotionEvent.Down
                    currentPosition = pointerInputChange.position
                    shapePosition = currentPosition
                    if (pointerInputChange.pressed != pointerInputChange.previousPressed) {
                        pointerInputChange.consume()
                    }
                },
                onDrag = { pointerInputChange ->
                    motionEvent = MotionEvent.Move
                    currentPosition = pointerInputChange.position

                    if (currentPathProperty().shapeMode != null) {
                        shapePosition?.let { start ->
                            previewDrawableShape = when (currentPathProperty().shapeMode) {
                                ShapeType.Rectangle -> {
                                    val width = currentPosition.x - start.x
                                    val height = currentPosition.y - start.y
                                    RectDrawableShape(start, width, height)
                                }

                                ShapeType.Circle -> {
                                    val radius = (currentPosition - start).getDistance()
                                    CircleDrawableShape(center = start, radius = radius)
                                }

                                ShapeType.Triangle -> {
                                    val point1 = start
                                    val point2 = Offset(currentPosition.x, start.y)
                                    val point3 = Offset(
                                        (start.x + currentPosition.x) / 2,
                                        start.y - (currentPosition.x - start.x) / 2
                                    )
                                    TriangleDrawableShape(point1, point2, point3)
                                }

                                ShapeType.Arrow -> {
                                    ArrowDrawableShape(start, currentPosition)
                                }

                                else -> null
                            }
                        }
                    } else {
                        if (drawMode == DrawMode.Touch) {
                            val change = pointerInputChange.positionChange()
                            path.forEach { entry ->
                                val path: Path = entry.first
                                path.translate(change)
                            }
                            currentPath.translate(change)
                        }
                    }

                    if (pointerInputChange.pressed != pointerInputChange.previousPressed) {
                        pointerInputChange.consume()
                    }
                },
                onDragEnd = { pointerInputChange ->
                    motionEvent = MotionEvent.Up
                    if (currentPathProperty().shapeMode != null) {
                        shapePosition?.let { start ->
                            val end = currentPosition
                            val finalShape = when (currentPathProperty().shapeMode) {
                                ShapeType.Rectangle -> {
                                    val width = end.x - start.x
                                    val height = end.y - start.y
                                    RectDrawableShape(start, width, height)
                                }

                                ShapeType.Circle -> {
                                    val radius = (end - start).getDistance()
                                    CircleDrawableShape(
                                        center = start,
                                        radius = radius
                                    )
                                }

                                ShapeType.Triangle -> {
                                    val point1 = start
                                    val point2 = Offset(end.x, start.y)
                                    val point3 = Offset(
                                        (start.x + end.x) / 2,
                                        start.y - (end.x - start.x) / 2
                                    )
                                    TriangleDrawableShape(point1, point2, point3)
                                }

                                ShapeType.Arrow -> {
                                    ArrowDrawableShape(start, end)
                                }

                                else -> null
                            }

                            finalShape?.let { shape ->
                                val pathForShape = Path().apply {
                                    when (shape) {
                                        is RectDrawableShape -> {
                                            addRect(
                                                Rect(
                                                    offset = shape.topLeft,
                                                    size = Size(shape.width, shape.height)
                                                )
                                            )
                                        }

                                        is CircleDrawableShape -> {
                                            addOval(
                                                Rect(
                                                    center = shape.center,
                                                    radius = shape.radius
                                                )
                                            )
                                        }

                                        is TriangleDrawableShape -> {
                                            moveTo(shape.point1.x, shape.point1.y)
                                            lineTo(shape.point2.x, shape.point2.y)
                                            lineTo(shape.point3.x, shape.point3.y)
                                            close()
                                        }

                                        is ArrowDrawableShape -> {
                                            moveTo(shape.start.x, shape.start.y)
                                            lineTo(shape.end.x, shape.end.y)

                                            val angle = atan2(
                                                (shape.end.y - shape.start.y).toDouble(),
                                                (shape.end.x - shape.start.x).toDouble()
                                            )
                                            val arrowHeadAngleRad =
                                                Math.toRadians(shape.arrowHeadAngle.toDouble())

                                            val arrowPoint1 = Offset(
                                                (shape.end.x - shape.arrowHeadLength * cos(
                                                    angle - arrowHeadAngleRad
                                                )).toFloat(),
                                                (shape.end.y - shape.arrowHeadLength * sin(
                                                    angle - arrowHeadAngleRad
                                                )).toFloat()
                                            )
                                            val arrowPoint2 = Offset(
                                                (shape.end.x - shape.arrowHeadLength * cos(
                                                    angle + arrowHeadAngleRad
                                                )).toFloat(),
                                                (shape.end.y - shape.arrowHeadLength * sin(
                                                    angle + arrowHeadAngleRad
                                                )).toFloat()
                                            )

                                            lineTo(arrowPoint1.x, arrowPoint1.y)
                                            moveTo(shape.end.x, shape.end.y)
                                            lineTo(arrowPoint2.x, arrowPoint2.y)
                                        }
                                    }
                                }

                                path.add(
                                    Pair(
                                        pathForShape,
                                        currentPathProperty(),
                                    )
                                )
                            }
                        }

                        previewDrawableShape = null
                        shapePosition = null

                        pathsUndone[pagerState.currentPage].clear()

                        currentPosition = Offset.Unspecified
                        previousPosition = currentPosition
                        motionEvent = MotionEvent.Idle
                    } else {
                        if (drawMode != DrawMode.Touch) {
                            currentPath.lineTo(currentPosition.x, currentPosition.y)

                            path.add(Pair(currentPath, currentPathProperty()))

                            currentPath = Path()

                            onCurrentPathPropertyChange(
                                PathProperties(
                                    strokeWidth = currentPathProperty().strokeWidth,
                                    color = currentPathProperty().color,
                                    strokeCap = currentPathProperty().strokeCap,
                                    strokeJoin = currentPathProperty().strokeJoin,
                                    eraseMode = currentPathProperty().eraseMode,
                                    shapeMode = currentPathProperty().shapeMode,
                                )
                            )
                        }

                        pathsUndone[pagerState.currentPage].clear()

                        currentPosition = Offset.Unspecified
                        previousPosition = currentPosition
                        motionEvent = MotionEvent.Idle
                    }

                    if (pointerInputChange.pressed != pointerInputChange.previousPressed) {
                        pointerInputChange.consume()
                    }
                })

        Canvas(modifier = drawModifier) {

            when (motionEvent) {

                MotionEvent.Down -> {
                    if (currentPathProperty().shapeMode == null && drawMode != DrawMode.Touch) {
                        currentPath.moveTo(currentPosition.x, currentPosition.y)
                    }

                    previousPosition = currentPosition
                }

                MotionEvent.Move -> {
                    if (currentPathProperty().shapeMode == null && drawMode != DrawMode.Touch) {
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
                                width = property.strokeWidth,
                                cap = property.strokeCap,
                                join = property.strokeJoin
                            ),
                            blendMode = BlendMode.Clear
                        )
                    }
                }

                previewDrawableShape?.let { shape ->
                    when (shape) {
                        is RectDrawableShape -> {
                            drawRect(
                                color = currentPathProperty().color.copy(alpha = 0.3f),
                                topLeft = shape.topLeft,
                                size = Size(shape.width, shape.height),
                                style = Stroke(
                                    width = currentPathProperty().strokeWidth,
                                    cap = currentPathProperty().strokeCap,
                                    join = currentPathProperty().strokeJoin
                                )
                            )
                        }

                        is CircleDrawableShape -> {
                            drawCircle(
                                color = currentPathProperty().color.copy(alpha = 0.3f),
                                center = shape.center,
                                radius = shape.radius,
                                style = Stroke(
                                    width = currentPathProperty().strokeWidth,
                                    cap = currentPathProperty().strokeCap,
                                    join = currentPathProperty().strokeJoin
                                )
                            )
                        }

                        is ArrowDrawableShape -> {
                            drawLine(
                                color = currentPathProperty().color.copy(alpha = 0.3f),
                                start = shape.start,
                                end = shape.end,
                                strokeWidth = currentPathProperty().strokeWidth,
                                cap = StrokeCap.Square
                            )

                            val angle = atan2(
                                (shape.end.y - shape.start.y).toDouble(),
                                (shape.end.x - shape.start.x).toDouble()
                            )
                            val arrowHeadAngleRad = Math.toRadians(shape.arrowHeadAngle.toDouble())
                            val arrowPoint1 = Offset(
                                (shape.end.x - shape.arrowHeadLength * cos(angle - arrowHeadAngleRad)).toFloat(),
                                (shape.end.y - shape.arrowHeadLength * sin(angle - arrowHeadAngleRad)).toFloat()
                            )
                            val arrowPoint2 = Offset(
                                (shape.end.x - shape.arrowHeadLength * cos(angle + arrowHeadAngleRad)).toFloat(),
                                (shape.end.y - shape.arrowHeadLength * sin(angle + arrowHeadAngleRad)).toFloat()
                            )

                            drawLine(
                                color = currentPathProperty().color.copy(alpha = 0.3f),
                                start = shape.end,
                                end = arrowPoint1,
                                strokeWidth = currentPathProperty().strokeWidth,
                                cap = StrokeCap.Square
                            )
                            drawLine(
                                color = currentPathProperty().color.copy(alpha = 0.3f),
                                start = shape.end,
                                end = arrowPoint2,
                                strokeWidth = currentPathProperty().strokeWidth,
                                cap = StrokeCap.Square
                            )
                        }

                        is TriangleDrawableShape -> {
                            drawPath(
                                color = currentPathProperty().color.copy(alpha = 0.3f),
                                path = Path().apply {
                                    moveTo(shape.point1.x, shape.point1.y)
                                    lineTo(shape.point2.x, shape.point2.y)
                                    lineTo(shape.point3.x, shape.point3.y)
                                    close()
                                },
                                style = Stroke(
                                    width = currentPathProperty().strokeWidth,
                                    cap = StrokeCap.Square,
                                    join = StrokeJoin.Miter
                                )
                            )
                        }
                    }
                }

                if (motionEvent != MotionEvent.Idle && currentPathProperty().shapeMode == null) {
                    if (!currentPathProperty().eraseMode) {
                        drawPath(
                            color = currentPathProperty().color,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathProperty().strokeWidth,
                                cap = currentPathProperty().strokeCap,
                                join = currentPathProperty().strokeJoin
                            )
                        )
                    } else {
                        drawPath(
                            color = Color.Transparent,
                            path = currentPath,
                            style = Stroke(
                                width = currentPathProperty().strokeWidth,
                                cap = currentPathProperty().strokeCap,
                                join = currentPathProperty().strokeJoin
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
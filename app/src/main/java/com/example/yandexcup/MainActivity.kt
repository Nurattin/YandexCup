package com.example.yandexcup

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.yandexcup.FPS.Companion.frameDelay
import com.example.yandexcup.components.ActionToolMode.*
import com.example.yandexcup.components.ColorSelectionDialog
import com.example.yandexcup.components.FloatingActionTools
import com.example.yandexcup.components.FrameDeleteDialog
import com.example.yandexcup.components.FrameGenerateDialog
import com.example.yandexcup.components.MovieBottomBar
import com.example.yandexcup.components.MovieCanvas
import com.example.yandexcup.components.MovieTopBar
import com.example.yandexcup.components.PropertiesMenuDialog
import com.example.yandexcup.core.ui.CornerRadii
import com.example.yandexcup.core.ui.FrameGenerator
import com.example.yandexcup.core.ui.PathProperties
import com.example.yandexcup.core.ui.ShapeType
import com.example.yandexcup.core.ui.SnapEdge
import com.example.yandexcup.core.ui.SnapEdge.Companion.snapToNearestEdge
import com.example.yandexcup.ui.theme.YandexCupTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
                detectDarkMode = {
                    true
                }
            ),
        )
        setContent {

            YandexCupTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    DrawingApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawingApp() {
    val density = LocalDensity.current

    val frameScrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val frames = remember {
        mutableStateListOf(mutableStateListOf<Pair<Path, PathProperties>>())
    }
    var currentPathProperty by remember {
        mutableStateOf(PathProperties())
    }
    val pathsUndone = remember {
        mutableStateListOf(mutableStateListOf<Pair<Path, PathProperties>>())
    }

    val pagerState = rememberPagerState(
        pageCount = frames::size
    )

    var isAnimate by remember {
        mutableStateOf(false)
    }

    var canvasSize by remember {
        mutableStateOf(IntSize.Zero)
    }

    val frameGenerator = remember(canvasSize) {
        FrameGenerator(
            canvasWidth = canvasSize.width.toFloat(),
            canvasHeight = canvasSize.height.toFloat(),
        )
    }

    var toolsSize by remember {
        mutableStateOf(IntSize.Zero)
    }
    val toolsOffsetX = remember { Animatable(0f) }
    val toolsOffsetY = remember { Animatable(400f) }

    var colorSelectedDialogIsVisible by remember {
        mutableStateOf(false)
    }
    var pathPropertyDialogIsVisible by remember {
        mutableStateOf(false)
    }
    var removeFrameDialogIsVisible by remember {
        mutableStateOf(false)
    }
    var autoGenerateDialogIsVisible by remember {
        mutableStateOf(false)
    }

    var fps by remember {
        mutableStateOf(FPS.F10)
    }

    LaunchedEffect(isAnimate) {
        while (isAnimate && pagerState.pageCount > 0) {
            val targetPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.scrollToPage(targetPage)
            launch {
                frameScrollState.animateScrollToItem(index = targetPage)
            }
            delay(fps.frameDelay())
        }
    }

    FrameDeleteDialog(
        visible = removeFrameDialogIsVisible,
        onDismiss = {
            removeFrameDialogIsVisible = false
        },
        onDeleteAllFrames = {
            frames.clear()
            pathsUndone.clear()
            removeFrameDialogIsVisible = false
        },
        onDeleteCurrentFrame = {
            frames.removeAt(pagerState.currentPage)
            pathsUndone.removeAt(pagerState.currentPage)
            removeFrameDialogIsVisible = false
        }
    )

    FrameGenerateDialog(
        visible = autoGenerateDialogIsVisible,
        onDismiss = {
            autoGenerateDialogIsVisible = false
        },
        onGenerateFrames = { count ->
            val newFrame = frameGenerator.generateFrames(count)
                .map(List<Pair<Path, PathProperties>>::toMutableStateList)
                .toMutableStateList()
            frames.addAll(
                index = pagerState.currentPage,
                elements = newFrame,
            )
            pathsUndone.addAll(
                index = pagerState.currentPage,
                elements = List(newFrame.size) { mutableStateListOf() },
            )
            autoGenerateDialogIsVisible = false
        }
    )


    ColorSelectionDialog(
        visible = colorSelectedDialogIsVisible,
        onDismiss = {
            colorSelectedDialogIsVisible = false
        },
        initialColor = currentPathProperty.color,
        onNegativeClick = {
            colorSelectedDialogIsVisible = false
        },
        onPositiveClick = { color ->
            currentPathProperty = currentPathProperty.copy(
                color = color,
            )
            colorSelectedDialogIsVisible = false
        }
    )

    PropertiesMenuDialog(
        visible = pathPropertyDialogIsVisible,
        pathOption = currentPathProperty,
        onDismiss = {
            pathPropertyDialogIsVisible = false
        },
        onPathOptionChange = {
            currentPathProperty = it
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(R.drawable.canvac),
                contentScale = ContentScale.FillBounds,
            )
    ) {
        MovieTopBar(
            modifier = Modifier,
            fps = fps,
            isAnimate = isAnimate,
            frameCount = frames.size,
            onChangeFpsClick = {
                fps = FPS.entries[(fps.ordinal + 1) % FPS.entries.size]
            },
            onRemoveFrameClick = {
                removeFrameDialogIsVisible = true
            },
            onPlayClick = {
                if (pagerState.pageCount > 0) {
                    isAnimate = !isAnimate
                }
            }
        )
        Box(
            modifier = Modifier
                .onSizeChanged {
                    canvasSize = it
                }
                .weight(1f)
        ) {
            MovieCanvas(
                modifier = Modifier
                    .fillMaxSize(),
                paths = frames,
                pathsUndone = pathsUndone,
                pagerState = pagerState,
                currentPathProperty = { currentPathProperty },
                isAnimate = isAnimate,
                onCurrentPathPropertyChange = { newPathProperty ->
                    currentPathProperty = newPathProperty
                },
            )
            var toolsAttachedEdge by remember {
                mutableStateOf<SnapEdge>(SnapEdge.LEFT)
            }

            val targetRadii = getCornerRadii(
                edge = toolsAttachedEdge,
            )

            val topStartRadius by animateDpAsState(
                targetValue = targetRadii.topStart,
                animationSpec = tween(durationMillis = 300),
                label = "topStartRadius",
            )
            val topEndRadius by animateDpAsState(
                targetValue = targetRadii.topEnd,
                animationSpec = tween(durationMillis = 300),
                label = "topEndRadius",
            )
            val bottomStartRadius by animateDpAsState(
                targetValue = targetRadii.bottomStart,
                animationSpec = tween(durationMillis = 300),
                label = "bottomStartRadius",
            )
            val bottomEndRadius by animateDpAsState(
                targetValue = targetRadii.bottomEnd,
                animationSpec = tween(durationMillis = 300),
                label = "bottomEndRadius",
            )

            val animatedShape = RoundedCornerShape(
                topStart = topStartRadius,
                topEnd = topEndRadius,
                bottomStart = bottomStartRadius,
                bottomEnd = bottomEndRadius
            )


            this@Column.AnimatedVisibility(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = toolsOffsetX.value.roundToInt(),
                            y = toolsOffsetY.value.roundToInt(),
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                scope.launch {
                                    val edge = snapToNearestEdge(
                                        offsetX = toolsOffsetX,
                                        offsetY = toolsOffsetY,
                                        parentSize = canvasSize,
                                        componentSize = toolsSize,
                                    )
                                    toolsAttachedEdge = edge
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    toolsOffsetX.snapTo(toolsOffsetX.value + dragAmount.x)
                                    toolsOffsetY.snapTo(toolsOffsetY.value + dragAmount.y)
                                }
                            },
                            onDragStart = {
                                toolsAttachedEdge = SnapEdge.NONE
                            }
                        )
                    }
                    .onSizeChanged { size ->
                        toolsSize = size
                    },
                visible = !isAnimate,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FloatingActionTools(
                    modifier = Modifier

                        .background(
                            color = Color.Black,
                            shape = animatedShape,
                        )
                        .padding(4.dp),
                    toolMode = when {
                        currentPathProperty.shapeMode != null -> Shape
                        currentPathProperty.eraseMode -> Erase
                        !currentPathProperty.eraseMode -> Pencil
                        else -> None
                    },
                    properties = currentPathProperty,
                    selectedShape = currentPathProperty.shapeMode,
                    onToolClick = { toolMode ->
                        when (toolMode) {
                            Erase -> {
                                currentPathProperty = currentPathProperty.copy(
                                    eraseMode = true,
                                    shapeMode = null,
                                )
                            }

                            Pencil -> {
                                currentPathProperty = currentPathProperty.copy(
                                    eraseMode = false,
                                    shapeMode = null,
                                )
                            }

                            AutoGenerate -> {
                                autoGenerateDialogIsVisible = true
                            }

                            None -> Unit
                            ColorPicker -> Unit
                            Shape -> {
                                currentPathProperty = currentPathProperty.copy(
                                    shapeMode = ShapeType.None,
                                    eraseMode = false,
                                )
                            }
                        }
                    },
                    onColorPickerClick = {
                        colorSelectedDialogIsVisible = true
                    },
                    onPathPickerClick = {
                        pathPropertyDialogIsVisible = true
                    },
                    onShapePickerClick = { newShape ->
                        currentPathProperty = currentPathProperty.copy(
                            shapeMode = newShape,
                            eraseMode = false,
                        )
                    }
                )
            }
        }
        val currentPage = pagerState.currentPage
        AnimatedVisibility(
            visible = !isAnimate,
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        ) {
            MovieBottomBar(
                undoEnabled = !frames.getOrNull(currentPage).isNullOrEmpty(),
                redoEnabled = !pathsUndone.getOrNull(currentPage).isNullOrEmpty(),
                pagerState = pagerState,
                frameScrollState = frameScrollState,
                frames = frames,
                onUndoClick = {
                    val path = frames[currentPage]
                    if (path.isNotEmpty()) {
                        val lastItem = path.last()
                        val lastPath = lastItem.first
                        val lastPathProperty = lastItem.second
                        path.remove(lastItem)

                        pathsUndone[currentPage].add(Pair(lastPath, lastPathProperty))
                    }
                },
                onRedoClick = {
                    val path = pathsUndone[currentPage]
                    if (path.isNotEmpty()) {

                        val lastPath = path.last().first
                        val lastPathProperty = path.last().second
                        path.removeAt(path.lastIndex)
                        frames[currentPage].add(Pair(lastPath, lastPathProperty))
                    }
                },
                onAddNewFrame = {
                    scope.launch {
                        if (frames.isEmpty()) {
                            frames.add(mutableStateListOf())
                            pathsUndone.add(mutableStateListOf())
                        } else {
                            frames.add(pagerState.currentPage + 1, mutableStateListOf())
                            pathsUndone.add(
                                pagerState.currentPage + 1,
                                mutableStateListOf()
                            )
                        }
                        pagerState.scrollToPage(pagerState.currentPage + 1)
                        frameScrollState.animateScrollBy(with(density) { (160.dp + 4.dp).toPx() })
                    }
                },
                onCopyClick = { page ->
                    frames.add(page + 1, frames[page].toMutableStateList())
                    pathsUndone.add(page + 1, mutableStateListOf())
                },
                onDeleteClick = { page ->
                    frames.removeAt(page)
                    pathsUndone.removeAt(page)
                },
                onFrameClick = { page ->
                    scope.launch {
                        pagerState.scrollToPage(page)
                    }
                },
                onFrameLongClick = { page ->
                    scope.launch {
                        pagerState.scrollToPage(page)
                        frameScrollState.animateScrollToItem(page)
                    }
                }
            )
        }
    }
}


@Composable
fun getCornerRadii(edge: SnapEdge?): CornerRadii {
    return when (edge) {
        SnapEdge.LEFT -> CornerRadii(
            topStart = 0.dp,
            topEnd = 12.dp,
            bottomStart = 0.dp,
            bottomEnd = 12.dp
        )

        SnapEdge.RIGHT -> CornerRadii(
            topStart = 12.dp,
            topEnd = 0.dp,
            bottomStart = 12.dp,
            bottomEnd = 0.dp
        )

        SnapEdge.TOP -> CornerRadii(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 12.dp,
            bottomEnd = 12.dp
        )

        SnapEdge.BOTTOM -> CornerRadii(
            topStart = 12.dp,
            topEnd = 12.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )

        SnapEdge.NONE -> CornerRadii(
            topStart = 12.dp,
            topEnd = 12.dp,
            bottomStart = 12.dp,
            bottomEnd = 12.dp
        )

        null -> CornerRadii(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        )
    }
}

enum class FPS(val count: Int) {
    F5(5),
    F10(10),
    F15(15),
    F30(30),
    F60(60),
    F120(120);

    companion object {
        fun FPS.frameDelay(): Long {
            return (1000 / count).toLong()
        }
    }
}



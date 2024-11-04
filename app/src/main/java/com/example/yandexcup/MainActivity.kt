package com.example.yandexcup

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yandexcup.components.ActionToolMode.*
import com.example.yandexcup.components.AddNewFrame
import com.example.yandexcup.components.ColorSelectionDialog
import com.example.yandexcup.components.FloatingActionTools
import com.example.yandexcup.components.FrameTab
import com.example.yandexcup.components.MovieCanvas
import com.example.yandexcup.components.PropertiesMenuDialog
import com.example.yandexcup.components.RedoAction
import com.example.yandexcup.components.UndoAction
import com.example.yandexcup.coreUi.CornerRadii
import com.example.yandexcup.coreUi.PathProperties
import com.example.yandexcup.coreUi.SnapEdge
import com.example.yandexcup.coreUi.snapToNearestEdge
import com.example.yandexcup.ui.theme.YandexCupTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            YandexCupTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    DrawingApp(
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawingApp() {
    val paths = remember {
        mutableStateListOf(mutableStateListOf<Pair<Path, PathProperties>>())
    }

    val pathsUndone = remember {
        mutableStateListOf(mutableStateListOf<Pair<Path, PathProperties>>())
    }

    var currentPathProperty by remember { mutableStateOf(PathProperties()) }
    val pagerState = rememberPagerState {
        paths.size
    }

    var isAnimate by remember {
        mutableStateOf(false)
    }

    val frameScrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var parentSize by remember { mutableStateOf(IntSize.Zero) }

    var toolsSize by remember { mutableStateOf(IntSize.Zero) }
    var colorSelectedDialogIsVisible by remember { mutableStateOf(false) }
    var pathPropertyDialogIsVisible by remember { mutableStateOf(false) }

    val toolsOffsetX = remember { Animatable(0f) }
    val toolsOffsetY = remember { Animatable(400f) }
    var fps by remember { mutableStateOf(FPS.F60) }

    LaunchedEffect(isAnimate) {
        while (isAnimate) {
            val targetPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.scrollToPage(targetPage)
            launch {
                frameScrollState.animateScrollToItem(index = targetPage)
            }
            delay((1000 / fps.count).toLong())
        }
    }


    if (colorSelectedDialogIsVisible) {
        ColorSelectionDialog(
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
    }

    if (pathPropertyDialogIsVisible) {
        PropertiesMenuDialog(
            pathOption = currentPathProperty,
            onDismiss = {
                pathPropertyDialogIsVisible = false
            },
            onPathOptionChange = {
                currentPathProperty = it
            }
        )
    }

    Column {
        Row(
            modifier = Modifier
                .background(Color.Black)
                .statusBarsPadding()
                .fillMaxWidth()
        ) {
            Row {

            }
            Spacer(
                modifier = Modifier
                    .weight(1f)
            )
            Row(
                modifier = Modifier
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .height(46.dp)
                        .border(
                            width = 1.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            onClick = {
                                fps = FPS.entries[(fps.ordinal + 1) % FPS.entries.size]
                            }
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 8.dp),
                        text = "FPS: ${fps.count}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .size(46.dp)
                        .border(
                            width = 1.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            onClick = {
                                isAnimate = !isAnimate
                            }
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(if (isAnimate) R.drawable.ic_round_pause else R.drawable.ic_baseline_play_arrow),
                        contentDescription = null,
                        tint = Color.White,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .onSizeChanged {
                    parentSize = it
                }
                .weight(1f)
        ) {

            MovieCanvas(
                modifier = Modifier
                    .paint(
                        painter = painterResource(R.drawable.canvac),
                        contentScale = ContentScale.FillBounds,
                    )
                    .fillMaxSize(),
                paths = paths,
                pathsUndone = pathsUndone,
                pagerState = pagerState,
                currentPathProperty = currentPathProperty,
                isAnimate = isAnimate,
                onCurrentPathPropertyChange = { newPathProperty ->
                    currentPathProperty = newPathProperty
                },
            )
            var attachedEdge by remember { mutableStateOf<SnapEdge?>(SnapEdge.LEFT) }

            val targetRadii = getCornerRadii(attachedEdge)

            val topStartRadius by animateDpAsState(
                targetValue = targetRadii.topStart,
                animationSpec = tween(durationMillis = 300)
            )
            val topEndRadius by animateDpAsState(
                targetValue = targetRadii.topEnd,
                animationSpec = tween(durationMillis = 300)
            )
            val bottomStartRadius by animateDpAsState(
                targetValue = targetRadii.bottomStart,
                animationSpec = tween(durationMillis = 300)
            )
            val bottomEndRadius by animateDpAsState(
                targetValue = targetRadii.bottomEnd,
                animationSpec = tween(durationMillis = 300)
            )

            val animatedShape = RoundedCornerShape(
                topStart = topStartRadius,
                topEnd = topEndRadius,
                bottomStart = bottomStartRadius,
                bottomEnd = bottomEndRadius
            )

            FloatingActionTools(
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
                                        parentSize = parentSize,
                                        componentSize = toolsSize
                                    )
                                    attachedEdge = edge
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
                                attachedEdge = SnapEdge.NONE
                            }
                        )
                    }
                    .onSizeChanged { size ->
                        toolsSize = size
                    }
                    .background(
                        color = Color.Black,
                        shape = animatedShape,
                    )
                    .padding(4.dp),
                toolMode = when (currentPathProperty.eraseMode) {
                    true -> Erase
                    false -> Pencil
                    else -> None
                },
                properties = currentPathProperty,
                onToolClick = { toolMode ->
                    when (toolMode) {
                        Erase -> {
                            currentPathProperty = currentPathProperty.copy(
                                eraseMode = true
                            )
                        }

                        Pencil -> {
                            currentPathProperty = currentPathProperty.copy(
                                eraseMode = false
                            )
                        }

                        None -> Unit
                        ColorPicker -> Unit
                    }
                },
                onColorPickerClick = {
                    colorSelectedDialogIsVisible = true
                },
                onPathPickerClick = {
                    pathPropertyDialogIsVisible = true
                }
            )
        }
        Row(
            modifier = Modifier
                .background(Color.Black)
                .navigationBarsPadding()
                .padding(10.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            val scrollState = rememberScrollState()
            val density = LocalDensity.current
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentPage = pagerState.currentPage
                UndoAction(
                    enabled = !paths.getOrNull(currentPage).isNullOrEmpty(),
                    onClick = {
                        val path = paths[currentPage]
                        if (path.isNotEmpty()) {
                            val lastItem = path.last()
                            val lastPath = lastItem.first
                            val lastPathProperty = lastItem.second
                            path.remove(lastItem)

                            pathsUndone[currentPage].add(Pair(lastPath, lastPathProperty))
                        }
                    },
                )
                RedoAction(
                    enabled = !pathsUndone.getOrNull(currentPage).isNullOrEmpty(),
                    onClick = {
                        val path = pathsUndone[currentPage]
                        if (path.isNotEmpty()) {

                            val lastPath = path.last().first
                            val lastPathProperty = path.last().second
                            path.removeAt(path.lastIndex)
                            paths[currentPage].add(Pair(lastPath, lastPathProperty))
                        }
                    },
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f),
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(
                        horizontal = 4.dp,
                    ),
                    state = frameScrollState,
                ) {
                    items(pagerState.pageCount) { page ->
                        val selected = page == pagerState.currentPage
                        FrameTab(
                            modifier = Modifier,
                            selected = selected,
                            number = page,
                            path = paths[page],
                            onClick = {
                                scope.launch {
                                    pagerState.scrollToPage(page)
                                }
                            },
                            onCopyClick = { page ->
                                paths.add(page + 1, paths[page].toMutableStateList())
                                pathsUndone.add(page + 1, mutableStateListOf())
                            },
                            onDeleteClick = { page ->
                                paths.removeAt(page)
                                pathsUndone.removeAt(page)
                            },
                            onLongClick = {
                                scope.launch {
                                    pagerState.scrollToPage(page)
                                    frameScrollState.animateScrollToItem(page)
                                }
                            }
                        )
                    }
                    item {
                        AddNewFrame(
                            modifier = Modifier
                        ) {
                            scope.launch {
                                paths.add(pagerState.currentPage + 1, mutableStateListOf())
                                pathsUndone.add(pagerState.currentPage + 1, mutableStateListOf())
                                pagerState.scrollToPage(pagerState.currentPage + 1)
                                scrollState.animateScrollBy(with(density) { (scrollState.value.dp + 160.dp + 4.dp).toPx() })
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .width(50.dp)
                        .align(Alignment.BottomStart)
                        .background(
                            brush = Brush.horizontalGradient(listOf(Color.Black, Color.Transparent))
                        )
                )
            }
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
    F5(5), F10(10), F15(15), F30(30), F60(60), F120(120),
}



package com.example.yandexcup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.example.yandexcup.core.ui.PathProperties

@Composable
fun MovieBottomBar(
    modifier: Modifier = Modifier,
    undoEnabled: Boolean,
    redoEnabled: Boolean,
    pagerState: PagerState,
    frameScrollState: LazyListState,
    frames: List<List<Pair<Path, PathProperties>>>,
    onFrameClick: (Int) -> Unit,
    onFrameLongClick: (Int) -> Unit,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onAddNewFrame: () -> Unit,
    onCopyClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit,
) {
    Row(
        modifier = modifier
            .background(Color.Black)
            .navigationBarsPadding()
            .padding(10.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UndoAction(
                enabled = undoEnabled,
                onClick = onUndoClick,
            )
            RedoAction(
                enabled = redoEnabled,
                onClick = onRedoClick,
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
                        path = frames[page],
                        onClick = {
                            onFrameClick(page)
                        },
                        onCopyClick = onCopyClick,
                        onDeleteClick = onDeleteClick,
                        onLongClick = {
                            onFrameLongClick(page)
                        }
                    )
                }
                item {
                    AddNewFrame(
                        modifier = Modifier,
                        onClick = onAddNewFrame,
                    )
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
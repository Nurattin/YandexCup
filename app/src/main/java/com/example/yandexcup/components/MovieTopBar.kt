package com.example.yandexcup.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yandexcup.FPS
import com.example.yandexcup.R

@Composable
fun MovieTopBar(
    modifier: Modifier = Modifier,
    fps: FPS,
    frameCount: Int,
    isAnimate: Boolean,
    onRemoveFrameClick: () -> Unit,
    onChangeFpsClick: () -> Unit,
    onPlayClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .background(Color.Black)
            .statusBarsPadding()
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        AnimatedVisibility(
            visible = !isAnimate && frameCount >= 1,
            modifier = Modifier,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
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
                        onClick = onRemoveFrameClick,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_clear),
                    contentDescription = null,
                    tint = Color.White,
                )
            }
        }
        Spacer(
            modifier = Modifier
                .weight(1f)
        )
        Row(
            modifier = Modifier,
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
                        onClick = onChangeFpsClick
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
                        onClick = onPlayClick,
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
}
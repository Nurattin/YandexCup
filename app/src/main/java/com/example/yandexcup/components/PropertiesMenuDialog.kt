package com.example.yandexcup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.yandexcup.coreUi.PathProperties

@Composable
fun PropertiesMenuDialog(
    pathOption: PathProperties,
    onPathOptionChange: (PathProperties) -> Unit,
    onDismiss: () -> Unit,
) {

    var strokeWidth by remember { mutableFloatStateOf(pathOption.strokeWidth) }
    var strokeCap by remember { mutableStateOf(pathOption.strokeCap) }
    var strokeJoin by remember { mutableStateOf(pathOption.strokeJoin) }

    Dialog(onDismissRequest = onDismiss) {

        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {

                Text(
                    text = "СВОЙСТВА",
                    color = Color.Blue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp, top = 12.dp)
                )

                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .height(40.dp)
                        .fillMaxWidth(),

                    ) {
                    val path = Path()
                    path.moveTo(0f, size.height / 2)
                    path.lineTo(size.width, size.height / 2)

                    drawPath(
                        color = pathOption.color,
                        path = path,
                        style = Stroke(
                            width = strokeWidth,
                            cap = strokeCap,
                            join = strokeJoin
                        )
                    )
                }

                Text(
                    text = "ТОЛЩИНА ${strokeWidth.toInt()}",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Slider(
                    value = strokeWidth,
                    onValueChange = {
                        strokeWidth = it
                        onPathOptionChange(pathOption.copy(strokeWidth = strokeWidth))
                    },
                    valueRange = MIN_PATH_STROKE_WIDTH..MAX_PATH_STROKE_WIDTH,
                    onValueChangeFinished = {}
                )
            }
        }
    }
}

const val MAX_PATH_STROKE_WIDTH = 100f
const val MIN_PATH_STROKE_WIDTH = 1f
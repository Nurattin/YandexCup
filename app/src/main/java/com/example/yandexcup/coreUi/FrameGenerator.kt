package com.example.yandexcup.coreUi

import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Path
import kotlin.random.Random

class FrameGenerator(
    private val canvasWidth: Float,
    private val canvasHeight: Float
) {
    private val shapes = listOf("CIRCLE", "SQUARE", "TRIANGLE")

    fun generateFrames(n: Int): List<List<Pair<Path, PathProperties>>> {
        val frames = mutableListOf<List<Pair<Path, PathProperties>>>()

        repeat(n) {
            val frame = generateSingleFrame()
            frames.add(frame)
        }

        return frames
    }

    private fun generateSingleFrame(): List<Pair<Path, PathProperties>> {
        val framePaths = mutableListOf<Pair<Path, PathProperties>>()
        val numberOfShapes = Random.nextInt(1, 50)

        repeat(numberOfShapes) {
            val shapeType = shapes.random()
            val path = when (shapeType) {
                "CIRCLE" -> createCirclePath()
                "SQUARE" -> createSquarePath()
                "TRIANGLE" -> createTrianglePath()
                else -> createCirclePath()
            }

            val properties = PathProperties(
                strokeWidth = Random.nextFloat() * 10f + 5f,
                color = Color(
                    red = Random.nextFloat(),
                    green = Random.nextFloat(),
                    blue = Random.nextFloat(),
                    alpha = 1f
                ),
                alpha = 1f,
                strokeCap = StrokeCap.Round,
                strokeJoin = StrokeJoin.Round,
                eraseMode = false
            )

            framePaths.add(Pair(path, properties))
        }

        return framePaths
    }

    private fun createCirclePath(): Path {
        val radius = Random.nextFloat() * 50f + 20f
        val centerX = Random.nextFloat() * (canvasWidth - 2 * radius) + radius
        val centerY = Random.nextFloat() * (canvasHeight - 2 * radius) + radius

        return Path().apply {
            addOval(
                Rect(
                    center = Offset(
                        centerX,
                        centerY,
                    ),
                    radius = radius,
                )
            )
        }
    }

    private fun createSquarePath(): Path {
        val size = Random.nextFloat() * 100f + 50f
        val left = Random.nextFloat() * (canvasWidth - size)
        val top = Random.nextFloat() * (canvasHeight - size)

        return Path().apply {
            addRect(
                Rect(
                    left = left,
                    top = top,
                    right = left + size,
                    bottom = top + size,
                )
            )
        }
    }

    private fun createTrianglePath(): Path {
        val size = Random.nextFloat() * 100f + 50f
        val left = Random.nextFloat() * (canvasWidth - size)
        val top = Random.nextFloat() * (canvasHeight - size)

        return Path().apply {
            moveTo(left + size / 2, top)
            lineTo(left, top + size)
            lineTo(left + size, top + size)
            close()
        }
    }
}

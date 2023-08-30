package com.developerpaul123.starview

import android.graphics.CornerPathEffect
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.toComposePathEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.developerpaul123.starview.common.FillDirection
import com.developerpaul123.starview.engine.StarEngine

/**
 * Copyright Paul 2023
 * Part of the StarView project
 */
@Composable
fun Star(
    points: Int,
    outlineColor: Color,
    fillColor: Color,
    backgroundColor: Color,
    fillPercent: Float,
    cornerRadius: Float,
    outlineThickness: Float,
    fillDirection: FillDirection
) {
    val effect = CornerPathEffect(cornerRadius).toComposePathEffect()
    val fillPaint = remember {
        Paint().apply {
            pathEffect = effect
            color = fillColor
            style = PaintingStyle.Fill
        }
    }

    val outlinePaint = remember {
        Paint().apply {
            pathEffect = effect
            color = outlineColor
            style = PaintingStyle.Stroke
            strokeWidth = outlineThickness
        }
    }

    val backgroundPaint = remember {
        Paint().apply {
            pathEffect = effect
            color = backgroundColor
            style = PaintingStyle.Fill
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .defaultMinSize(100.dp, 100.dp),
            onDraw = {
                val width = size.width
                val height = size.height
                // get the path
                val graphicsPath =
                    StarEngine.computeStarPath(
                        points,
                        width.toInt(),
                        height.toInt()
                    )
                val bounds = RectF()
                graphicsPath.computeBounds(bounds, true)

                val path = graphicsPath.asComposePath()

                with(drawContext.canvas) {
                    // draw background
                    drawPath(
                        path,
                        backgroundPaint
                    )

                    // determine clipping based on fill direction
                    val left: Float
                    val right: Float
                    val top: Float
                    val bottom: Float
                    when (fillDirection) {
                        FillDirection.LeftToRight -> {
                            left = 0f
                            right = bounds.width() * fillPercent
                            top = 0f
                            bottom = bounds.height()
                        }

                        FillDirection.RightToLeft -> {
                            left = width * (1 - fillPercent)
                            right = width
                            top = 0f
                            bottom = height
                        }

                        FillDirection.TopToBottom -> {
                            left = 0f
                            right = width
                            top = 0f
                            bottom = height * fillPercent
                        }

                        FillDirection.BottomToTop -> {
                            left = 0f
                            right = bounds.width()
                            top = height * (1 - fillPercent)
                            bottom = height
                        }

                    }
                    clipRect(left, top, right, bottom) {
                        drawPath(
                            path,
                            fillPaint
                        )
                    }

                    // draw the outline
                    drawPath(path, outlinePaint)
                }
            }
        )
    }
}

@Composable
@Preview
private fun StarPreview() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(100.dp),
        modifier = Modifier.fillMaxSize(),
        // content padding
        contentPadding = PaddingValues(
            start = 8.dp,
            top = 16.dp,
            end = 8.dp,
            bottom = 8.dp
        ),
        content = {
            items(8) { point ->
                Star(
                    point + 3,
                    outlineColor = Color(252, 212, 52),
                    fillColor = Color(252, 182, 52),
                    backgroundColor = Color.Transparent,
                    0.5f,
                    80f,
                    10f,
                    fillDirection = FillDirection.LeftToRight
                )
            }
        }
    )
}

@Composable
@Preview
private fun SimpleStar() {
    Star(
        5,
        outlineColor = Color(252, 212, 52),
        fillColor = Color(252, 182, 52),
        backgroundColor = Color.Transparent,
        0.5f,
        80f,
        20f,
        fillDirection = FillDirection.BottomToTop
    )
}
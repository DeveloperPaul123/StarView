package com.developerpaul123.starview

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.developerpaul123.starview.engine.StarEngine
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Copyright Paul 2021
 * Part of the StarView project
 */
class StarView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    private val cornerRadius = 50f
    private val cornerPathEffect = CornerPathEffect(cornerRadius)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 2f
        color = Color.rgb(252, 202, 52)
        pathEffect = cornerPathEffect
    }

    var numberOfPoints = 5

    init {
        setLayerType(LAYER_TYPE_HARDWARE, paint)
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
        val starPath = getStarPath()
        paint.style = Paint.Style.FILL_AND_STROKE
        canvas?.drawPath(starPath, paint)
    }

    private fun getStarPath(): Path {
        return StarEngine.computeStarPath(numberOfPoints, width = width, height = height)
    }
}
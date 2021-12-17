package com.developerpaul123.starview.engine

import android.graphics.Path
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Copyright Paul 2021
 * Part of the StarView project
 */
class StarEngine {
    companion object {
        fun computeStarPath(
            numberOfPoints: Int,
            width: Int,
            height: Int,
            innerToOuterRadiusRatio: Float = 1 / 3f
        ): Path {

            val radius = (width / 2) * 0.95
            val path = Path()
            val halfPi = PI / 2
            val centerX = width / 2f
            val centerY = height / 2f
            val numberOfVertices = 2 * numberOfPoints
            val radianSpacing = (2f * PI) / numberOfVertices

            for (i in 0 until numberOfVertices + 1) {
                val degreesInRad = i * radianSpacing
                var radiusModifier = 1f
                if (i % 2 != 0) {
                    radiusModifier = innerToOuterRadiusRatio
                }

                val x = centerX + (radius * radiusModifier) * cos(degreesInRad + 3 * halfPi)
                val y = centerY + (radius * radiusModifier) * sin(degreesInRad + 3 * halfPi)
                if (i == 0) {
                    path.moveTo(x.toFloat(), y.toFloat())
                    continue
                }
                path.lineTo(x.toFloat(), y.toFloat())
            }

            path.close()
            return path
        }
    }
}
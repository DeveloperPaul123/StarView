package com.developerpaul123.starview.engine

import android.graphics.Path
import android.graphics.PointF
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Copyright Paul 2021-23
 * Part of the StarView project
 *
 * This class computes the paths that are drawn as stars in the views and composable views.
 */
class StarEngine {
    companion object {
        private fun computeStarPoints(
            numberOfPoints: Int,
            width: Int,
            height: Int,
            innerToOuterRadiusRatio: Float
        ): List<PointF> {

            val dim = if (width > 0 && height > 0) minOf(width, height) else width
            val radius = (dim / 2) * 0.95
            val halfPi = PI / 2
            val centerX = width / 2f
            val centerY = height / 2f
            val numberOfVertices = 2 * numberOfPoints
            val radianSpacing = (2f * PI) / numberOfVertices

            val points = mutableListOf<PointF>()
            for (i in 0 until numberOfVertices + 1) {
                val degreesInRad = i * radianSpacing
                var radiusModifier = 1f
                if (i % 2 != 0) {
                    radiusModifier = innerToOuterRadiusRatio
                }

                val x = centerX + (radius * radiusModifier) * cos(degreesInRad + 3 * halfPi)
                val y = centerY + (radius * radiusModifier) * sin(degreesInRad + 3 * halfPi)
                points.add(PointF(x.toFloat(), y.toFloat()))

            }
            return points
        }

        fun computeStarPath(
            numberOfPoints: Int,
            width: Int,
            height: Int,
            innerToOuterRadiusRatio: Float = 1 / 3f
        ): Path {

            val path = Path()
            val points = computeStarPoints(numberOfPoints, width, height, innerToOuterRadiusRatio)
            var isFirst = true
            points.forEach { point ->
                if (isFirst) {
                    path.moveTo(point.x, point.y)
                    isFirst = false
                } else {
                    path.lineTo(point.x, point.y)
                }
            }
            path.close()
            return path
        }
    }
}
package com.developerpaul123.starview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.math.MathUtils
import com.developerpaul123.starview.engine.StarEngine

/**
 * Copyright Paul 2021
 * Part of the StarView project
 */
class StarView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    enum class FillDirection {
        LeftToRight,
        RightToLeft,
        TopToBottom,
        BottomToTop
    }

    /**
     * @brief Fill color of the star
     */
    var starColor = Color.rgb(252, 202, 52)
        set(value) {
            field = value
            starPaint.color = value
            invalidate()
        }

    /**
     * @brief Color of the outline of the star
     */
    var starOutlineColor = starColor
        set(value) {
            field = value
            starOutlinePaint.color = value
            invalidate()
        }

    /**
     * @brief Background color of the star (unfilled color)
     */
    var starBackgroundColor = Color.WHITE
        set(value) {
            field = value
            backgroundPaint.color = value
            invalidate()
        }

    /**
     * @brief Thickness of the star outline
     */
    var outlineThickness = 4f
        set(value) {
            field = value
            invalidate()
        }

    /**
     * @brief Number of points of the star
     */
    var numberOfPoints = 5
        set(value) {
            field = value
            invalidate()
        }

    /**
     * @brief Percentage of the star to be filled. Value is clamped to be between 0.0 and 1.0
     */
    var fillPercentage = 1.0f
        set(value) {
            field = MathUtils.clamp(value, 0.0f, 1.0f)
            invalidate()
        }

    /**
     * @brief The direction that the star should be filled.
     */
    var fillDirection = FillDirection.LeftToRight
        set(value) {
            field = value
            invalidate()
        }

    /**
     * @brief Corner radius of the star
     */
    var cornerRadius = 10.0f
        set(value) {
            field = value
            starPaint.pathEffect = CornerPathEffect(cornerRadius)
            starOutlinePaint.pathEffect = CornerPathEffect(cornerRadius)
            backgroundPaint.pathEffect = CornerPathEffect(cornerRadius)
            invalidate()
        }

    private lateinit var starBitmap: Bitmap
    private lateinit var backgroundBitmap: Bitmap
    private lateinit var starCanvas: Canvas
    private lateinit var backgroundCanvas: Canvas
    private val fillRect = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val starPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = outlineThickness
        color = starColor
        pathEffect = CornerPathEffect(cornerRadius)
    }

    private val starOutlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeWidth = outlineThickness
        color = starOutlineColor
        pathEffect = CornerPathEffect(cornerRadius)
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeCap = Paint.Cap.ROUND
        color = starBackgroundColor
        pathEffect = CornerPathEffect(cornerRadius)
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, paint)
        createBitmapResources()

        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.StarView,
            0, 0
        ).apply {
            try {
                val fillDirectionIndex = getInteger(
                    R.styleable.StarView_fillDirection,
                    FillDirection.LeftToRight.ordinal
                )
                fillDirection = FillDirection.values()[fillDirectionIndex]
                fillPercentage = getFloat(R.styleable.StarView_fillPercentage, 1.0f)

                starColor = getColor(R.styleable.StarView_starColor, Color.rgb(252, 202, 52))
                starBackgroundColor =
                    getColor(R.styleable.StarView_starBackgroundColor, Color.WHITE)
                starOutlineColor = getColor(R.styleable.StarView_starOutlineColor, starColor)
                numberOfPoints = getInteger(R.styleable.StarView_numberOfPoints, 5)
                outlineThickness = getFloat(R.styleable.StarView_outlineThickness, 4.0f)
            } finally {
                recycle()
            }
        }
    }

    private fun createBitmapResources() {
        if (width > 0 && height > 0) {
            starBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        } else {
            starBitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888)
            backgroundBitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888)
        }

        starCanvas = Canvas(starBitmap)
        backgroundCanvas = Canvas(backgroundBitmap)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createBitmapResources()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        // draw the star (background)
        val starPath = getStarPath()
        drawStarBitmapBackground(backgroundPaint, starPath)

        val bounds = RectF()
        starPath.computeBounds(bounds, true)
        // draw the fill
        drawStarFill(starPaint, bounds)

        // paint the bitmaps as masks
        canvas?.drawBitmap(backgroundBitmap, 0f, 0f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas?.drawBitmap(starBitmap, 0f, 0f, paint)
        paint.xfermode = null

        // draw the outline
        canvas?.drawPath(starPath, starOutlinePaint)
    }

    private fun getStarPath(): Path {
        return StarEngine.computeStarPath(numberOfPoints, width = width, height = height)
    }

    private fun drawStarBitmapBackground(paint: Paint, starPath: Path) {
        if (this::starCanvas.isInitialized) {
            starCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            starCanvas.drawPath(starPath, paint)
        }
    }

    private fun drawStarFill(paint: Paint, pathBounds: RectF) {
        if (this::backgroundCanvas.isInitialized) {
            backgroundCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            val width = backgroundBitmap.width.toFloat()
            val height = backgroundBitmap.height.toFloat()

            val isWidthDirection =
                fillDirection == FillDirection.LeftToRight || fillDirection == FillDirection.RightToLeft
            val dimension =
                if (isWidthDirection) width else height
            val dimensionTerminus = fillPercentage * dimension

            fillRect.set(pathBounds)

            when (fillDirection) {
                FillDirection.LeftToRight -> {
                    fillRect.right = dimensionTerminus
                }
                FillDirection.RightToLeft -> {
                    fillRect.left = dimension - dimensionTerminus
                    fillRect.right = dimension
                }
                FillDirection.TopToBottom -> {
                    fillRect.bottom = dimensionTerminus
                }
                FillDirection.BottomToTop -> {
                    fillRect.top = dimension - dimensionTerminus
                    fillRect.bottom = dimension
                }
            }

            backgroundCanvas.drawRect(
                fillRect,
                paint
            )
        }
    }
}
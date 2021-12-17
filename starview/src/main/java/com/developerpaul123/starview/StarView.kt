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
            starBackgroundPaint.color = value
            invalidate()
        }

    /**
     * @brief Thickness of the star outline
     */
    var outlineThickness = 4f
        set(value) {
            field = value
            starOutlinePaint.strokeWidth = value
            invalidate()
        }

    /**
     * @brief Number of points of the star
     */
    var numberOfPoints = 5
        set(value) {
            field = value
            computeStarPath()
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
            starBackgroundPaint.pathEffect = CornerPathEffect(cornerRadius)
            invalidate()
        }

    private lateinit var starBitmap: Bitmap
    private lateinit var starBackgroundFillBitmap: Bitmap
    private lateinit var starCanvas: Canvas
    private lateinit var starBackgroundFillCanvas: Canvas
    private lateinit var starPath: Path
    private val starPathBounds = RectF()

    /// @brief Rect used for star fill
    private val fillRect = RectF()

    /// @brief paint for bitmaps only
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /// @brief Star fill paint
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

    private val starBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
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
                fillPercentage = getFloat(R.styleable.StarView_fillPercentage, 0.5f)

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

    private fun computeStarPath() {
        starPath = getStarPath()
        starPath.computeBounds(starPathBounds, true)
    }

    private fun createBitmapResources() {
        if (width > 0 && height > 0) {
            starBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            starBackgroundFillBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        } else {
            starBitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888)
            starBackgroundFillBitmap = Bitmap.createBitmap(20, 20, Bitmap.Config.ARGB_8888)
        }

        starCanvas = Canvas(starBitmap)
        starBackgroundFillCanvas = Canvas(starBackgroundFillBitmap)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createBitmapResources()
        computeStarPath()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        // draw the background color
        canvas?.drawPath(starPath, starBackgroundPaint)

        // draw the star (background)
        drawStarBitmapBackground(starPaint, starPath)

        starPath.computeBounds(starPathBounds, true)
        // draw the fill
        drawStarFill(starPaint, starPathBounds)

        // paint the bitmaps as masks
        canvas?.drawBitmap(starBackgroundFillBitmap, 0f, 0f, paint)
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

    private fun drawStarFill(fillPaint: Paint, pathBounds: RectF) {
        if (this::starBackgroundFillCanvas.isInitialized) {
            starBackgroundFillCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            val width = starBackgroundFillBitmap.width.toFloat()
            val height = starBackgroundFillBitmap.height.toFloat()

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

            starBackgroundFillCanvas.drawRect(
                fillRect,
                fillPaint
            )
        }
    }
}
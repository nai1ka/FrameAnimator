package ru.ndevelop.yandexcup2024.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import ru.ndevelop.yandexcup2024.R
import ru.ndevelop.yandexcup2024.ui.models.Frame
import ru.ndevelop.yandexcup2024.ui.models.LinePath
import kotlin.math.abs


class DrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paths = mutableListOf<LinePath>()
    private val redoPaths = mutableListOf<LinePath>()


    var onDrawingFinishListener: ((Frame) -> Unit)? = null

    var onCanvasLoaded: ((Frame) -> Unit)? = null
    private var currentPath = Path()
    private lateinit var canvasBitmap: Bitmap

    val canvasHeight get() = canvasBitmap.height
    val canvasWidth get() = canvasBitmap.width


    init {
        setBackgroundResource(R.drawable.drawing_background)



        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    var currentColor = Color.BLACK
        set(value) {
            field = value
            drawingPaint.color = value
        }


    var currentX = 0
    var currentY = 0

    private val drawingPaint = Paint().apply {
        color = currentColor
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        strokeWidth = 10f
        isAntiAlias = true
    }

    private val eraserPaint = Paint().apply {
        color = Color.TRANSPARENT
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        style = Paint.Style.STROKE
        strokeWidth = 40f
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    private var currentPaint = drawingPaint

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d("DrawingView", "onSizeChanged: w=$w, h=$h")
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap)

        onCanvasLoaded?.invoke(getFrame())
    }


    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        Log.d("DrawingView", "onRestoreInstanceState")
    }

    private lateinit var drawCanvas: Canvas

    private var backgroundBitmap: Bitmap? = null

    private val backgroundPaint = Paint().apply {
        alpha = 100
    }

    private var isEraserOn = false

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        backgroundBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, backgroundPaint)
        }

        canvas.drawBitmap(canvasBitmap, 0f, 0f, null)
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath.reset()
                currentPath.moveTo(x, y)
                currentX = x.toInt()
                currentY = y.toInt()
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = abs(x - currentX)
                val dy = abs(y - currentY)
                if (dx >= 4 || dy >= 4) {
                    currentPath.quadTo(
                        currentX.toFloat(),
                        currentY.toFloat(),
                        (x + currentX) / 2,
                        (y + currentY) / 2
                    )
                    drawCanvas.drawPath(currentPath, currentPaint)
                    currentX = x.toInt()
                    currentY = y.toInt()
                }

            }

            MotionEvent.ACTION_UP -> {
                currentPath.lineTo(currentX.toFloat(), currentY.toFloat())
                drawCanvas.drawPath(currentPath, currentPaint)
                onDrawingFinishListener?.invoke(getFrame())
                paths.add(LinePath(currentPath, Paint(currentPaint)))
                redoPaths.clear()
                currentPath = Path()
            }
        }
        invalidate()
        return true
    }

    fun setEraserMode(isEraser: Boolean) {
        isEraserOn = isEraser
        if (isEraserOn) {
            currentPaint = eraserPaint
        } else {
            currentPaint = drawingPaint
        }
    }

    fun getFrame(): Frame {
        return Frame(bitmap = canvasBitmap.copy(Bitmap.Config.ARGB_8888, false))
    }

    fun clear() {
        paths.clear()
        redoPaths.clear()
        currentPath.reset()
        drawCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    fun setBitmap(bitmap: Bitmap?) {
        if (bitmap == null) return
        clear()
        drawCanvas.drawBitmap(bitmap, 0f, 0f, null)
        invalidate()
    }

    fun setBackgroundBitmap(bitmap: Bitmap?) {
        backgroundBitmap = bitmap
        invalidate()
    }


    fun undo() {
        if (paths.isNotEmpty()) {
            val lastPath = paths.removeAt(paths.size - 1)
            redoPaths.add(lastPath)
            drawCanvas.clear()
            for (path in paths) {
                drawCanvas.drawPath(path.path, path.paint)
            }
            invalidate()
        }
    }

    // Redo the last undone line
    fun redo() {
        if (redoPaths.isNotEmpty()) {
            val lastUndonePath = redoPaths.removeAt(redoPaths.size - 1)
            paths.add(lastUndonePath)
            for (path in paths) {
                drawCanvas.drawPath(path.path, path.paint)
            }
            invalidate() // Redraw the view
        }
    }

    fun Canvas.clear() {
        drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }

    var pencilThickness: Float
        get() = drawingPaint.strokeWidth
        set(value) {
            drawingPaint.strokeWidth = value
        }

    var eraserThickness: Float
        get() = eraserPaint.strokeWidth
        set(value) {
            eraserPaint.strokeWidth = value
        }
}

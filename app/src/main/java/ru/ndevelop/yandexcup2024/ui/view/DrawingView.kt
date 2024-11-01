package ru.ndevelop.yandexcup2024.ui.view

import android.R.attr.radius
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import ru.ndevelop.yandexcup2024.R
import ru.ndevelop.yandexcup2024.ui.models.Frame
import ru.ndevelop.yandexcup2024.ui.models.LinePath
import kotlin.math.abs


class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paths = mutableListOf<LinePath>()
    private val redoPaths = mutableListOf<LinePath>()
    private var currentPath = Path()
    private lateinit var canvasBitmap: Bitmap


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
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap)

        clipPath.reset()
        clipPath.addRoundRect(
            0f, 0f, w.toFloat(), h.toFloat(),
            cornerRadius, cornerRadius,
            Path.Direction.CW
        )
    }


    private lateinit var drawCanvas: Canvas

    private var backgroundBitmap: Bitmap? = null

    private val backgroundPaint = Paint().apply {
        alpha = 100
    }

    private var isEraserOn = false
    private val cornerRadius = 50f
    private val clipPath = Path()

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        canvas.clipPath(clipPath)
        super.onDraw(canvas)

        backgroundBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, backgroundPaint)
        }


        canvas.drawBitmap(canvasBitmap, 0f, 0f, null)
        canvas.restore()
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

    fun clear(restoreFrame: Frame? = null) {
        paths.clear()
        redoPaths.clear()
        currentPath.reset()
        drawCanvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR)

        if(restoreFrame != null) {
            drawCanvas.drawBitmap(restoreFrame.bitmap, 0f, 0f, null)
        }
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
}

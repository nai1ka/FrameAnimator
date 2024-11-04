package ru.ndevelop.yandexcup2024.ui.view

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import ru.ndevelop.yandexcup2024.R
import ru.ndevelop.yandexcup2024.ui.models.Frame

class FrameAnimatorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    val frameHandler = Handler(Looper.getMainLooper())
    val frameInterval = 100L

    var animationSpeedFactor: Float = 1.0f
        set(value) {
            field = value.coerceAtLeast(0.1f).coerceAtMost(2f)
            frameHandler.removeCallbacks(frameRunnable)
            frameHandler.post(frameRunnable)
        }
    private var frames: List<Frame> = emptyList()
    private var currentFrameIndex = 0

    init {
        setBackgroundResource(R.drawable.drawing_background)
    }

    private val frameRunnable = object : Runnable {
        override fun run() {
            currentFrameIndex = (currentFrameIndex + 1) % frames.size
            invalidate()
            frameHandler.postDelayed(this, (frameInterval / animationSpeedFactor).toLong())
        }
    }

    fun setFrames(frames: List<Frame>) {
        this.frames = frames
    }

    fun startAnimation() {
        currentFrameIndex = 0
        frameHandler.removeCallbacks(frameRunnable)
        frameHandler.post(frameRunnable)
    }

    fun stopAnimation() {
        frameHandler.removeCallbacks(frameRunnable)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (frames.isNotEmpty()) {
            // Draw the current frame bitmap at the center of the view
            val bitmap = frames[currentFrameIndex].bitmap
            val left = (width - bitmap.width) / 2f
            val top = (height - bitmap.height) / 2f
            canvas.drawBitmap(bitmap, left, top, null)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation() // Stop the animation when the view is detached
    }
}
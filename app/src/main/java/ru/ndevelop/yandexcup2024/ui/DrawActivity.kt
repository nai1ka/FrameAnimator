package ru.ndevelop.yandexcup2024.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.children
import androidx.core.view.isVisible
import ru.ndevelop.yandexcup2024.R
import ru.ndevelop.yandexcup2024.databinding.ActivityDrawBinding
import ru.ndevelop.yandexcup2024.ui.Utils.createCircleDrawable
import ru.ndevelop.yandexcup2024.ui.view.DrawingView
import ru.ndevelop.yandexcup2024.ui.view.FrameAnimatorView
import ru.ndevelop.yandexcup2024.ui.view.PaletteView

class DrawActivity : AppCompatActivity() {


    private lateinit var binding: ActivityDrawBinding

    val framesRepository = FramesRepository()

    private lateinit var drawingView: DrawingView

    private lateinit var btnAddFrame: View
    private lateinit var btnClear: View
    private lateinit var btnPencil: View
    private lateinit var btnEraser: View
    private lateinit var btnBrush: View
    private lateinit var btnUndo: View
    private lateinit var btnRedo: View
    private lateinit var llBottomButtons: LinearLayout

    private lateinit var btnPlay: View
    private lateinit var btnPause: View
    private lateinit var frameAnimatorView: FrameAnimatorView
    private lateinit var paletteView: PaletteView
    private lateinit var btnInstruments: View

    private val onColorClickListener = object : OnColorClickListener {
        override fun onColorClick(color: Int) {
            updateSelectedColor(color)
            paletteView.hidePalette()
            paletteView.isVisible = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDrawBinding.inflate(layoutInflater)
        setupViews()


        setContentView(binding.root)
    }

    private fun setupViews() {
        drawingView = binding.drawingView

        btnAddFrame = binding.btnFilePlus
        btnAddFrame.setOnClickListener {
            framesRepository.addFrame(drawingView.getFrame())

            drawingView.clear()
            drawingView.setBackgroundBitmap(framesRepository.getFrames().last().bitmap)
        }


        btnClear = binding.btnBin
        btnClear.setOnClickListener { drawingView.clear() }

        btnPencil = binding.btnPencil
        btnPencil.setOnClickListener { drawingView.setEraserMode(false) }

        btnEraser = binding.btnEraser
        btnEraser.setOnClickListener { drawingView.setEraserMode(true) }

        btnBrush = binding.btnBrush
        btnBrush.setOnClickListener { drawingView.setEraserMode(false) }

        btnRedo = binding.btnForward
        btnRedo.setOnClickListener { drawingView.redo() }

        btnUndo = binding.btnBack
        btnUndo.setOnClickListener { drawingView.undo() }

        btnPlay = binding.btnPlay
        btnPlay.setOnClickListener {
            drawingView.isVisible = false
            frameAnimatorView.isVisible = true
            frameAnimatorView.setFrames(framesRepository.getFrames())
            frameAnimatorView.startAnimation()
        }

        btnPause = binding.btnPause
        btnPause.setOnClickListener {
            drawingView.isVisible = true
            frameAnimatorView.isVisible = false
            frameAnimatorView.stopAnimation()
        }

        frameAnimatorView = binding.frameAnimatorView

        paletteView = binding.paletteView
        paletteView.onColorClickListener = onColorClickListener

        llBottomButtons = binding.llBottomButtons

        llBottomButtons.addView(ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(100, 100).apply {
                setMargins(resources.getDimensionPixelSize(R.dimen.buttons_distance), 0, 0, 0)
            }
            background = createCircleDrawable(Color.BLUE)
            setOnClickListener { paletteView.isVisible = !paletteView.isVisible }
        })
    }

    fun updateSelectedColor(color: Int) {
        drawingView.currentColor = color
        llBottomButtons.children.last().background = createCircleDrawable(color)
    }
}
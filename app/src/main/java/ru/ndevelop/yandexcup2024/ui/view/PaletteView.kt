package ru.ndevelop.yandexcup2024.ui.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.ndevelop.yandexcup2024.R
import ru.ndevelop.yandexcup2024.ui.OnColorClickListener
import ru.ndevelop.yandexcup2024.ui.Utils.createCircleDrawable
import ru.ndevelop.yandexcup2024.ui.adapters.PaletteRecyclerViewAdapter


class PaletteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,


    ) : LinearLayout(context, attrs, defStyleAttr) {

    private val quickColorContainer: LinearLayout


    var onColorClickListener: OnColorClickListener? = null
        set(value) {
            field = value
            paletteRecyclerViewAdapter.onColorClickListener = value
        }

    val listOfColors = listOf(
        0xFFFFFECC,
        0xFFFF95D5,
        0xFFFFD1A9,
        0xFFFEDCAF,
        0xFFCCF3FF,

        0xFFF3ED00,
        0xFFF8D3E3,
        0xFFFA9A46,
        0xFFB18CFE,
        0xFF94E4FD,

        0xFFA8DB10,
        0xFFFB66A4,
        0xFFFC7600,
        0xFF9747FF,
        0xFF00C9FB,

        0xFF75BB41,
        0xFFDC0057,
        0xFFED746C,
        0xFF4D21B2,
        0xFF73A8FC,

        0xFF4E7A25,
        0xFF9D234C,
        0xFFFF3D00,
        0xFF641580,
        0xFF1976D2,
    ).map { it.toInt() }

    private val paletteRecyclerViewAdapter = PaletteRecyclerViewAdapter(listOfColors)
    private var isExpanded = false
        set(value) {
            field = value
            rvPalette.isVisible = value
        }

    private val rvPalette: RecyclerView

    fun hidePalette() {
        isExpanded = false
    }

    init {

        LayoutInflater.from(context).inflate(R.layout.palette_view, this, true)
        quickColorContainer = findViewById(R.id.ll_quick_color)
        val btnPalette = ImageView(context).apply {
            layoutParams = LayoutParams(100, 100).apply {
                setMargins(8, 0, 16, 0)
            }
            background = AppCompatResources.getDrawable(context, R.drawable.ic_palette)
            setOnClickListener { isExpanded = !isExpanded }
        }

        rvPalette = findViewById(R.id.rv_palette)
        rvPalette.setLayoutManager(GridLayoutManager(context, 5))

        rvPalette.adapter = paletteRecyclerViewAdapter
        quickColorContainer.addView(btnPalette)
        addCirclesWithColors(listOf(Color.WHITE, Color.RED, Color.BLACK, Color.BLUE))
    }

    // Method to add circles with specified colors
    fun addCirclesWithColors(colors: List<Int>, size: Int = 100) {
        for (color in colors) {

            val circleView = ImageView(context).apply {
                layoutParams = LayoutParams(size, size).apply {
                    setMargins(8, 0, 16, 0)
                }
                background = createCircleDrawable(color)
                setOnClickListener { onColorClickListener?.onColorClick(color) }
            }

            // Add the circle view to the container
            quickColorContainer.addView(circleView)
        }
    }


}

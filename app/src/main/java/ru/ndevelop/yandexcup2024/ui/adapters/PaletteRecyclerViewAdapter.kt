package ru.ndevelop.yandexcup2024.ui.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.setMargins
import androidx.recyclerview.widget.RecyclerView
import ru.ndevelop.yandexcup2024.ui.OnColorClickListener
import ru.ndevelop.yandexcup2024.ui.Utils.createCircleDrawable

class PaletteRecyclerViewAdapter(private val colors: List<Int>) :
    RecyclerView.Adapter<PaletteRecyclerViewAdapter.ViewHolder>() {
        var onColorClickListener: OnColorClickListener? = null

    class ViewHolder(view: View, private val onColorClickListener: OnColorClickListener?) :
        RecyclerView.ViewHolder(view) {
        fun bind(color: Int) {
            (itemView as ImageView).apply {
                background = createCircleDrawable(color)
                setOnClickListener { onColorClickListener?.onColorClick(color) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ImageView(parent.context).apply {
            // TODO size to other place
            layoutParams = LinearLayout.LayoutParams(100, 100).apply {
                setMargins(16)
            }

        }

        return ViewHolder(view, onColorClickListener)
    }

    override fun getItemCount() = colors.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(colors[position])
    }
}
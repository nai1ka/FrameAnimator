package ru.ndevelop.yandexcup2024.ui

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable

object Utils {
    fun createCircleDrawable(color: Int): Drawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(color)
        }
    }
}
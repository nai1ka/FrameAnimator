package ru.ndevelop.yandexcup2024

import androidx.compose.ui.graphics.Color
import ru.ndevelop.yandexcup2024.ui.models.Frame

data class ScreenState(
    val isAnimating: Boolean = false,
    val selectedColor: Color = Color.Black,
    val isColorPickerOpened: Boolean = false,
    val isColorPickerExpanded: Boolean = false,
    val framesList: List<Frame> = emptyList(),
    val selectedFrameIndex: Int = 0,
    val pencilThickness: Float = 100f,
    val eraserThickness: Float = 400f,
    val selectedThicknessSelector: Instruments? = null,
    val isGifLoading: Boolean = false,
)

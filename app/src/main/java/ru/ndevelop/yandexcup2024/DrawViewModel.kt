package ru.ndevelop.yandexcup2024

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ru.ndevelop.yandexcup2024.ui.models.Frame

class DrawViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ScreenState())
    val uiState = _uiState.asStateFlow()

    fun setAnimation(isAnimating: Boolean) {
        _uiState.value = uiState.value.copy(isAnimating = isAnimating)
    }

    fun setSelectedColor(color: Color) {
        _uiState.value = uiState.value.copy(selectedColor = color)
    }

    fun setIsColorPickerOpened(isOpened: Boolean) {
        _uiState.value = uiState.value.copy(isColorPickerOpened = isOpened)
    }

    fun setIsColorPickerExpanded(isExpanded: Boolean) {
        _uiState.value = uiState.value.copy(isColorPickerExpanded = isExpanded)
    }

    fun setFramesList(framesList: List<Frame>) {
        _uiState.value = uiState.value.copy(framesList = framesList)
    }

    fun setSelectedFrameIndex(index: Int) {
        _uiState.value = uiState.value.copy(selectedFrameIndex = index)
    }
    fun setPencilThickness(thickness: Float) {
        _uiState.value = uiState.value.copy(pencilThickness = thickness)
    }

    fun setEraseThickness(thickness: Float) {
        _uiState.value = uiState.value.copy(eraserThickness = thickness)
    }

    fun setSelectedThicknessSelector(instrument: Instruments?) {
        _uiState.value = uiState.value.copy(selectedThicknessSelector = instrument)
    }

    fun setIsGifLoading(isLoading: Boolean) {
        _uiState.value = uiState.value.copy(isGifLoading = isLoading)
    }


}

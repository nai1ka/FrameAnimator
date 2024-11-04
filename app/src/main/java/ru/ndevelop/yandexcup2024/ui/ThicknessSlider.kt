package ru.ndevelop.yandexcup2024.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.ndevelop.yandexcup2024.Instruments

@Composable
fun ThicknessSlider(
    selectedThicknessSelector: Instruments?,
    modifier: Modifier = Modifier,
    pencilThickness: Float,
    eraserThickness: Float,
    onPenThicknessChange: (Float) -> Unit,
    onEraserThicknessChange: (Float) -> Unit
) {
    AnimatedVisibility(
        visible = selectedThicknessSelector != null,
        modifier = modifier,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Slider(
            value = when (selectedThicknessSelector) {
                Instruments.PENCIL -> pencilThickness
                Instruments.ERASER -> eraserThickness
                else -> 0f
            }, onValueChange = {
                if (selectedThicknessSelector == null) return@Slider
                when (selectedThicknessSelector) {
                    Instruments.PENCIL -> onPenThicknessChange(it)
                    Instruments.ERASER -> onEraserThicknessChange(it)
                }
            }, colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ), valueRange = 1f..500f
        )
    }
}
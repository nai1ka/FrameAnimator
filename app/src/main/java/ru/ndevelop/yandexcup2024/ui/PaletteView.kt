package ru.ndevelop.yandexcup2024.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.ndevelop.yandexcup2024.Dimensions

@Composable
fun PaletteView(isColorPickerExpanded: Boolean, onColorSelected: (Color) -> Unit) {
    AnimatedVisibility(
        visible = isColorPickerExpanded,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()

    ) {
        Box(
            modifier = Modifier
                .background(
                    Color(0xDB000000), shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                paletteColors.forEach { colorList ->
                    Row {
                        colorList.forEachIndexed { index, color ->
                            if (index > 0) Spacer(modifier = Modifier.width(Dimensions.iconsDistance))
                            ColorPicker(color) {
                                onColorSelected(color)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(Dimensions.iconsDistance))
                }
            }
        }
    }
}
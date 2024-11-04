package ru.ndevelop.yandexcup2024.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.ndevelop.yandexcup2024.Dimensions
import ru.ndevelop.yandexcup2024.R

@Composable
fun QuickPaletteView(onExpand: () -> Unit = {}, onColorSelected: (Color) -> Unit) {
    Box(
        modifier = Modifier
            .background(Color(0xDB000000), shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row {
            IconButton(
                painter = painterResource(id = R.drawable.ic_palette),
                contentDescription = "Back",
                tint = Color.White,
                onClick = onExpand
            )
            for (color in listOf(
                Color.White,
                Color.Red,
                Color.Black,
                Color.Blue,
            )) {
                Spacer(modifier = Modifier.width(Dimensions.iconsDistance))
                ColorPicker(color) {
                    onColorSelected(color)
                }
            }
        }
    }
}
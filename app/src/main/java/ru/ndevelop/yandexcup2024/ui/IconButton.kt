package ru.ndevelop.yandexcup2024.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconButton(
    painter: androidx.compose.ui.graphics.painter.Painter,
    contentDescription: String,
    clickable: Boolean = true,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onBackground,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit
) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
            .size(36.dp)
            .combinedClickable(
                enabled = clickable, onClick = onClick, onLongClick = onLongClick
            )
    )
}

@Composable
fun PressingIconButton(
    painter: androidx.compose.ui.graphics.painter.Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onBackground,
    onClick: () -> Unit,
    onPressed: () -> Unit,
    onReleased: () -> Unit,
    onMoved: (Float) -> Unit = {}

) {
    Icon(
        painter = painter,
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier
            .size(36.dp)
            .pointerInput(Unit) {
                while (true) {
                    awaitPointerEventScope {
                        val down = awaitFirstDown()
                        onClick()
                        onPressed()
                        drag(down.id) {
                            onMoved(it.positionChange().x)
                            it.consume()
                        }
                        onReleased()
                    }
                }
            },

        )
}
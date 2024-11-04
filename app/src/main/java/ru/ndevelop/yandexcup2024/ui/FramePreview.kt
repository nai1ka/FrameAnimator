package ru.ndevelop.yandexcup2024.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.ndevelop.yandexcup2024.R
import ru.ndevelop.yandexcup2024.ui.models.Frame

@Composable
fun FramePreview(
    frame: Frame, onDelete: () -> Unit = {}, onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(120.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(bitmap = frame.bitmap.asImageBitmap(),"", modifier = Modifier.size(120.dp), )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            painter = painterResource(id = R.drawable.ic_bin),
            contentDescription = "",
            tint = Color.Red,
            onClick = onDelete
        )
        Spacer(modifier = Modifier.width(16.dp))
    }
}

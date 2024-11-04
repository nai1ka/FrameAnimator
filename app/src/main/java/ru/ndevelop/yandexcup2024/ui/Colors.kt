package ru.ndevelop.yandexcup2024.ui

import androidx.compose.ui.graphics.Color

val paletteColors = listOf(
    listOf(
        0xFFFFFECC,
        0xFFFF95D5,
        0xFFFFD1A9,
        0xFFFEDCAF,
        0xFFCCF3FF,
    ),
    listOf(
        0xFFF3ED00,
        0xFFF8D3E3,
        0xFFFA9A46,
        0xFFB18CFE,
        0xFF94E4FD,
    ),
    listOf(
        0xFFA8DB10,
        0xFFFB66A4,
        0xFFFC7600,
        0xFF9747FF,
        0xFF00C9FB,
    ),
    listOf(
        0xFF75BB41,
        0xFFDC0057,
        0xFFED746C,
        0xFF4D21B2,
        0xFF73A8FC,
    ),
    listOf(
        0xFF4E7A25,
        0xFF9D234C,
        0xFFFF3D00,
        0xFF641580,
        0xFF1976D2,
    )
).map { intColor -> intColor.map { Color(it) } }
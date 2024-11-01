package ru.ndevelop.yandexcup2024.ui

import ru.ndevelop.yandexcup2024.ui.models.Frame

class FramesRepository {
    private val frames = mutableListOf<Frame>()

    fun addFrame(frame: Frame) {
        frames.add(frame)
    }

    fun getFrames(): List<Frame> {
        return frames
    }
}
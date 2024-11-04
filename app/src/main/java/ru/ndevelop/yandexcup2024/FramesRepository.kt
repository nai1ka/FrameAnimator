package ru.ndevelop.yandexcup2024

import ru.ndevelop.yandexcup2024.ui.models.Frame

class FramesRepository {
    private val frames = mutableListOf<Frame>()

    fun addFrame(frame: Frame) {
        frames.add(frame)
    }

    fun getFrames(): List<Frame> {
        return frames
    }

    fun removeFrame(index: Int) {
        if (index in frames.indices) {
            frames.removeAt(index)
        }
    }

    fun removeLastFrame() {
        if (frames.isNotEmpty()) {
            frames.removeAt(frames.size - 1)
        }
    }
}
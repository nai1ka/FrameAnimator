package ru.ndevelop.yandexcup2024

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.core.content.FileProvider
import ru.ndevelop.yandexcup2024.Utils.generateRandomColor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import kotlin.random.Random

object Utils {
    fun generateRandomColor(): Int {
        return android.graphics.Color.rgb(
            Random.nextInt(256),
            Random.nextInt(256),
            Random.nextInt(256)
        )
    }

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
    ).map { it.map { Color(it) } }
}

object GifUtils{
    suspend fun createGifByteArray(
        bitmaps: List<Bitmap>,
        delayMs: Int = 100,
        loop: Boolean = true
    ): ByteArray? {
        if (bitmaps.isEmpty()) return null

        val byteArrayOutputStream = ByteArrayOutputStream()
        try {
            val gifEncoder = AnimatedGifEncoder()
            gifEncoder.start(byteArrayOutputStream)
            gifEncoder.setRepeat(if (loop) 0 else -1)
            gifEncoder.setDelay(delayMs)

            for (bitmap in bitmaps) {
                gifEncoder.addFrame(bitmap)
            }

            gifEncoder.finish()
            return byteArrayOutputStream.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    fun shareGif(context: Context, gifData: ByteArray) {
        // Write the byte array to a temporary cache file
        val tempGifFile = File(context.cacheDir, "shared_image.gif")
        tempGifFile.outputStream().use { it.write(gifData) }

        // Get URI for the file
        val gifUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempGifFile
        )

        // Create the share intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/gif"
            putExtra(Intent.EXTRA_STREAM, gifUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }


        context.startActivity(Intent.createChooser(shareIntent, "Share GIF"))
    }
}

object RandomShapes {
    private fun drawRandomShape(canvas: Canvas, paint: Paint, width: Int, height: Int) {

        val shapeType = ShapeType.entries.random()

        val color = generateRandomColor()
        val x = Random.nextInt(width).toFloat()
        val y = Random.nextInt(height).toFloat()
        val rotation = Random.nextFloat() * 360
        val size = Random.nextInt(50, 150).toFloat()
        paint.color = color

        canvas.save()
        canvas.rotate(rotation, x, y)
        when (shapeType) {
            ShapeType.CIRCLE -> {
                canvas.drawCircle(x, y, size / 2, paint)
            }

            ShapeType.RECTANGLE -> {
                val rect = RectF(x, y, x + size, y + size)
                canvas.drawRect(rect, paint)
            }

            ShapeType.LINE -> {
                val endX = x + size * Random.nextFloat()
                val endY = y + size * Random.nextFloat()
                paint.strokeWidth = Random.nextInt(5, 15).toFloat()
                canvas.drawLine(x, y, endX, endY, paint)
            }
        }

        canvas.restore()
    }

    fun generateRandomShapesBitmap(width: Int, height: Int, shapeCount: Int): Bitmap {

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint().apply {
            isAntiAlias = true
        }

        repeat(shapeCount) {
            drawRandomShape(canvas, paint, width, height)
        }

        return bitmap
    }
}
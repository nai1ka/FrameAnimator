package ru.ndevelop.yandexcup2024.ui


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.ndevelop.yandexcup2024.R
import ru.ndevelop.yandexcup2024.ui.models.Frame
import ru.ndevelop.yandexcup2024.ui.view.DrawingView
import ru.ndevelop.yandexcup2024.ui.view.FrameAnimatorView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import kotlin.math.max
import kotlin.random.Random

enum class Instruments {
    PENCIL, ERASER
}

class DrawActivity : androidx.activity.ComponentActivity() {

    private fun generateRandomColor(): Int {
        return android.graphics.Color.rgb(
            Random.nextInt(256),
            Random.nextInt(256),
            Random.nextInt(256)
        )
    }

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


    enum class ShapeType { CIRCLE, RECTANGLE, LINE }

    private fun drawRandomShape(canvas: Canvas, paint: Paint, width: Int, height: Int) {
        // Randomly select a shape type
        val shapeType = ShapeType.entries.random()

        // Generate random properties for the shape
        val color = generateRandomColor()
        val x = Random.nextInt(width).toFloat()
        val y = Random.nextInt(height).toFloat()
        val rotation = Random.nextFloat() * 360
        val size = Random.nextInt(50, 150).toFloat()

        // Set the paint color
        paint.color = color

        // Save the current state of the canvas
        canvas.save()

        // Rotate canvas for the shape's random rotation
        canvas.rotate(rotation, x, y)

        // Draw the shape based on the random type
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

        // Restore the canvas to its previous state (before rotation)
        canvas.restore()
    }

    fun generateRandomShapesBitmap(width: Int, height: Int, shapeCount: Int): Bitmap {
        // Create a bitmap with the specified width and height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Create a paint object for drawing
        val paint = Paint().apply {
            isAntiAlias = true
        }

        // Draw the specified number of random shapes
        repeat(shapeCount) {
            drawRandomShape(canvas, paint, width, height)
        }

        return bitmap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            MainScreen()
        }
    }

    private val iconsDistance = 8.dp

    @Composable
    fun MainScreen() {
        val context = LocalContext.current
        val drawingViewInstance by remember { mutableStateOf(DrawingView(context)) }
        val frameAnimatorViewInstance by remember { mutableStateOf(FrameAnimatorView(context)) }
        var isAnimating by remember { mutableStateOf(false) }
        var selectedColor by remember { mutableStateOf(Color.Blue) }
        var isColorPickerOpened by remember { mutableStateOf(false) }
        var isColorPickerExpanded by remember { mutableStateOf(false) }
        var sliderPosition by remember { mutableFloatStateOf(1f) }
        var framesList by remember { mutableStateOf(emptyList<Frame>()) }
        var selectedFrameIndex by remember { mutableIntStateOf(0) }
        val coroutineScope = rememberCoroutineScope()

        var drawingViewCreated by remember { mutableStateOf(false) }

        var pencilThickness by remember { mutableStateOf(100f) }
        var eraserThickness by remember { mutableStateOf(400f) }
        var selectedThicknessSelector by remember { mutableStateOf<Instruments?>(null) }

        var isGifLoading by remember { mutableStateOf(false) }

        val alpha by animateFloatAsState(
            targetValue = if (!isAnimating) 1f else 0f,
            animationSpec = tween(durationMillis = 200), label = "Hide buttons when animating"
        )

        var showLayers by remember { mutableStateOf(false) }
        LaunchedEffect(selectedFrameIndex) {
            if (drawingViewCreated) {
                selectedFrameIndex = max(0, selectedFrameIndex)
                drawingViewInstance.clear()
                drawingViewInstance.setBackgroundBitmap(
                    framesList
                        .getOrNull(selectedFrameIndex - 1)?.bitmap
                )
                drawingViewInstance.setBitmap(framesList.getOrNull(selectedFrameIndex)?.bitmap)
            }
        }
        LaunchedEffect(pencilThickness, eraserThickness) {
            drawingViewInstance.pencilThickness = pencilThickness / 10
            drawingViewInstance.eraserThickness = eraserThickness / 10
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(16.dp)

        ) {
            CustomDialog(
                showDialog = showLayers,
                onDismiss = { showLayers = false },
                frames = framesList,
                onFrameClick = {
                    selectedFrameIndex = it
                },
                onDeleteFrame = { index ->
                    if (index in framesList.indices) {
                        framesList = framesList.toMutableList().apply {
                            removeAt(index)
                        }
                    }

                },
                onCreateRandomFrames = { numberOfRandomFrames ->
                    coroutineScope.launch {
                        val framesToAdd = mutableListOf<Frame>()
                        withContext(Dispatchers.Default) {
                            repeat(numberOfRandomFrames) {
                                framesToAdd.add(
                                    Frame(
                                        bitmap = generateRandomShapesBitmap(
                                            drawingViewInstance.canvasWidth,
                                            drawingViewInstance.canvasHeight,
                                            (5..10).random()
                                        )
                                    )
                                )
                            }
                            framesList = framesList + framesToAdd
                            selectedFrameIndex = framesList.size
                        }


                    }

                }
            )

            AnimatedVisibility(
                visible = selectedThicknessSelector != null,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp)
                    .padding(horizontal = 20.dp)

                    .zIndex(10f),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            )
            {
                Slider(
                    value = when (selectedThicknessSelector) {
                        Instruments.PENCIL -> pencilThickness
                        Instruments.ERASER -> eraserThickness
                        else -> 0f
                    },
                    onValueChange = {
                        if (selectedThicknessSelector == null) return@Slider
                        when (selectedThicknessSelector!!) {
                            Instruments.PENCIL -> pencilThickness = it
                            Instruments.ERASER -> eraserThickness = it
                        }
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    valueRange = 1f..500f
                )


            }


            AnimatedVisibility(
                visible = isColorPickerOpened,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp)
                    .zIndex(10f),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Box {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        val colors = listOf(
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

                        AnimatedVisibility(
                            visible = isColorPickerExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()

                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color(0xDB000000),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Column {
                                    colors.forEach { colorList ->
                                        Row {
                                            colorList.forEachIndexed { index, color ->
                                                if (index > 0)
                                                    Spacer(modifier = Modifier.width(iconsDistance))
                                                ColorPicker(color) {
                                                    selectedColor = color
                                                    drawingViewInstance.currentColor =
                                                        color.toArgb()
                                                    isColorPickerOpened = false
                                                    isColorPickerExpanded = false
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(iconsDistance))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xDB000000), shape = RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Row {
                                IconButton(
                                    painter = painterResource(id = R.drawable.ic_palette),
                                    contentDescription = "Back"
                                ) {
                                    isColorPickerExpanded = !isColorPickerExpanded
                                }
                                for (color in listOf(
                                    Color.White,
                                    Color.Red,
                                    Color.Black,
                                    Color.Blue,
                                )) {
                                    Spacer(modifier = Modifier.width(iconsDistance))
                                    ColorPicker(color) {
                                        selectedColor = color
                                        drawingViewInstance.currentColor = color.toArgb()
                                        isColorPickerOpened = false
                                        isColorPickerExpanded = false
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row {
                    IconButton(
                        modifier = Modifier.alpha(alpha),
                        clickable = !isAnimating,
                        painter = painterResource(id = R.drawable.ic_arrow_back_active),
                        contentDescription = "Back"
                    ) {
                        drawingViewInstance.undo()
                    }
                    Spacer(modifier = Modifier.width(iconsDistance))


                    IconButton(
                        modifier = Modifier.alpha(alpha),
                        clickable = !isAnimating,
                        painter = painterResource(id = R.drawable.ic_arrow_forward_active),
                        contentDescription = "Back"
                    ) {
                        drawingViewInstance.redo()
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        modifier = Modifier.alpha(alpha),
                        clickable = !isAnimating,
                        painter = painterResource(id = R.drawable.ic_bin),
                        contentDescription = "Back",
                        onLongClick = {
                            framesList = emptyList()
                            selectedFrameIndex = 0
                        }
                    ) {
                        if (selectedFrameIndex in framesList.indices) {
                            framesList = framesList.toMutableList().apply {
                                removeAt(selectedFrameIndex)
                            }
                        }
                        selectedFrameIndex -= 1
                    }
                    Spacer(modifier = Modifier.width(iconsDistance))
                    IconButton(
                        modifier = Modifier.alpha(alpha),
                        clickable = !isAnimating,
                        painter = painterResource(id = R.drawable.ic_file_plus),
                        contentDescription = "Back"
                    ) {
                        selectedFrameIndex += 1
                    }
                    Spacer(modifier = Modifier.width(iconsDistance))
                    IconButton(
                        modifier = Modifier.alpha(alpha),
                        clickable = !isAnimating,
                        painter = painterResource(id = R.drawable.ic_layers),
                        contentDescription = "Back"
                    ) {
                        showLayers = true
                    }
                    Spacer(modifier = Modifier.width(iconsDistance))
                    IconButton(
                        modifier = Modifier.alpha(alpha),
                        clickable = !isAnimating,
                        painter = painterResource(id = R.drawable.ic_copy),
                        contentDescription = "Back"
                    ) {
                        // Add element after selectedFrameIndex
                        if (selectedFrameIndex in framesList.indices) {
                            framesList = framesList.toMutableList().apply {
                                add(selectedFrameIndex, framesList[selectedFrameIndex])
                            }
                        }
                        selectedFrameIndex += 1
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(
                        painter = painterResource(id = R.drawable.ic_pause),
                        contentDescription = "Back"
                    ) {
                        isAnimating = false
                        frameAnimatorViewInstance.stopAnimation()

                    }
                    Spacer(modifier = Modifier.width(iconsDistance))
                    IconButton(
                        painter = painterResource(id = R.drawable.ic_play),
                        contentDescription = "Back"
                    ) {
                        isAnimating = true
                        isColorPickerOpened = false
                        isColorPickerExpanded = false
                        frameAnimatorViewInstance.setFrames(framesList)
                        frameAnimatorViewInstance.startAnimation()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),

                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(Modifier.fillMaxWidth()) {
                        if (isAnimating) {
                            AndroidView(factory = {

                                frameAnimatorViewInstance

                            })
                        } else {
                            AndroidView(factory = {
                                drawingViewInstance.onDrawingFinishListener = {
                                    framesList = framesList.toMutableList().apply {
                                        if (selectedFrameIndex in framesList.indices)
                                            set(selectedFrameIndex, it)
                                        else
                                            add(it)
                                    }
                                }
                                drawingViewInstance.onCanvasLoaded = {
                                    drawingViewCreated = true
                                    framesList = listOf(it)
                                }
                                drawingViewInstance
                            })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.height(50.dp)) {
                    Row(modifier = Modifier.alpha(alpha)) {
                        PressingIconButton(
                            modifier = Modifier,
                            painter = painterResource(id = R.drawable.ic_pencil),
                            contentDescription = "Back",
                            onClick = { drawingViewInstance.setEraserMode(false) },
                            onPressed = {
                                selectedThicknessSelector = Instruments.PENCIL
                            },
                            onReleased = {
                                selectedThicknessSelector = null
                            },
                            onMoved = { change ->
                                pencilThickness += change

                            }
                        )
                        Spacer(modifier = Modifier.width(iconsDistance))
//                        IconButton(
//                            clickable = !isAnimating,
//                            painter = painterResource(id = R.drawable.ic_brush),
//                            contentDescription = "Back"
//                        ) {
//                            drawingViewInstance.setEraserMode(false)
//                        }
                        PressingIconButton(
                            modifier = Modifier,
                            painter = painterResource(id = R.drawable.ic_eraser),
                            contentDescription = "Back",
                            onClick = { drawingViewInstance.setEraserMode(true) },
                            onPressed = {
                                selectedThicknessSelector = Instruments.ERASER
                            },
                            onReleased = {
                                selectedThicknessSelector = null
                            },
                            onMoved = { change ->
                                eraserThickness += change

                            }
                        )
                        Spacer(modifier = Modifier.width(iconsDistance))
                        IconButton(
                            clickable = !isAnimating,
                            painter = painterResource(id = R.drawable.ic_instruments),
                            contentDescription = "Back"
                        ) {

                        }
                        Spacer(modifier = Modifier.width(iconsDistance))
                        ColorPicker(selectedColor, clickable = !isAnimating) {

                            isColorPickerOpened = !isColorPickerOpened
                            isColorPickerExpanded = false
                        }
                        Spacer(modifier = Modifier.width(iconsDistance))
                        if (isGifLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.width(36.dp),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        } else {
                            IconButton(
                                painter = painterResource(id = R.drawable.ic_share),
                                contentDescription = "Share"
                            ) {
                                isGifLoading = true
                                coroutineScope.launch {
                                    val gifData = withContext(Dispatchers.Default) {
                                        createGifByteArray(
                                            framesList.map {
                                                it.bitmap.copy(
                                                    Bitmap.Config.ARGB_8888,
                                                    false
                                                )
                                            },
                                            delayMs = 100,
                                            loop = true
                                        )
                                    }
                                    isGifLoading = false
                                    if (gifData != null) {
                                        withContext(Dispatchers.IO) {
                                            shareGif(context, gifData)
                                        }
                                    }
                                }
                            }
                        }

                    }
                    if (alpha == 0f) {
                        Slider(
                            value = sliderPosition,
                            onValueChange = {
                                sliderPosition =
                                    it; frameAnimatorViewInstance.animationSpeedFactor = it
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.secondary,
                                activeTrackColor = MaterialTheme.colorScheme.secondary,
                                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            valueRange = 0.1f..2f
                        )
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun PreviewMainScreen() {
        MainScreen()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconButton(
    painter: androidx.compose.ui.graphics.painter.Painter,
    contentDescription: String,
    clickable: Boolean = true,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
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
                enabled = clickable,
                onClick = onClick,
                onLongClick = onLongClick
            )
    )
}

@Composable
fun PressingIconButton(
    painter: androidx.compose.ui.graphics.painter.Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    tint: Color = Color.White,
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

@Composable
fun ColorPicker(
    color: Color,
    clickable: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(color, shape = CircleShape)
            .clickable(enabled = clickable, onClick = onClick)
    )
}

@Composable
fun CustomDialog(
    showDialog: Boolean,
    onFrameClick: (Int) -> Unit,
    onDismiss: () -> Unit,
    frames: List<Frame>,
    onDeleteFrame: (Int) -> Unit,
    onCreateRandomFrames: (Int) -> Unit
) {

    var createRandomFrames by remember { mutableStateOf(false) }
    var selectedNumberOfRandomFrames by remember { mutableIntStateOf(1) }
    if (showDialog) {

        Dialog(onDismissRequest = {
            createRandomFrames = false
            selectedNumberOfRandomFrames = 1
            onDismiss()
        }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = Color.White,
                modifier = Modifier

                    .padding(16.dp)
                    .fillMaxWidth()

            ) {
                if (createRandomFrames) {
                    Column(
                        modifier = Modifier

                            .padding(16.dp)
                    ) {
                        Text("Введите количество кадров")
                        Text("Кадры будут созданы случайно")
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = selectedNumberOfRandomFrames.toString(),
                            onValueChange = { input ->
                                // Only allow numeric input
                                if (input.all { it.isDigit() }) {
                                    selectedNumberOfRandomFrames = input.toInt()
                                }
                            },

                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = {

                            onCreateRandomFrames(selectedNumberOfRandomFrames)
                            createRandomFrames = false
                            selectedNumberOfRandomFrames = 1
                            onDismiss()

                        }, modifier = Modifier.align(Alignment.End)) {
                            Text("Создать")
                        }
                    }

                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        LazyColumn {
                            itemsIndexed(frames) { index, item ->
                                FramePreview(item, onClick = {
                                    onFrameClick(index)
                                    onDismiss()

                                }, onDelete = {
                                    onDeleteFrame(index)
                                })
                                HorizontalDivider()
                            }
                        }

                        Text(
                            text = "Добавить несколько кадров",
                            modifier = Modifier
                                .padding(16.dp)
                                .clickable {
                                    createRandomFrames = true
                                },
                            color = Color.Blue
                        )
                    }
                }


            }
        }
    }
}

@Composable
fun FramePreview(
    frame: Frame,
    onDelete: () -> Unit = {},
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(bitmap = frame.bitmap.asImageBitmap(), "")
        IconButton(
            painter = painterResource(id = R.drawable.ic_bin),
            contentDescription = "Delete",
            modifier = Modifier.size(50.dp),
            onClick = {}
        )
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
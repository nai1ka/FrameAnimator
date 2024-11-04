package ru.ndevelop.yandexcup2024.ui

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.ndevelop.yandexcup2024.Dimensions
import ru.ndevelop.yandexcup2024.DrawViewModel
import ru.ndevelop.yandexcup2024.GifUtils.createGifByteArray
import ru.ndevelop.yandexcup2024.GifUtils.shareGif
import ru.ndevelop.yandexcup2024.Instruments
import ru.ndevelop.yandexcup2024.R
import ru.ndevelop.yandexcup2024.RandomShapes.generateRandomShapesBitmap
import ru.ndevelop.yandexcup2024.ui.models.Frame
import ru.ndevelop.yandexcup2024.ui.view.DrawingView
import ru.ndevelop.yandexcup2024.ui.view.FrameAnimatorView
import kotlin.math.max
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun MainScreen(drawingViewModel: DrawViewModel = viewModel()
) {
    val context = LocalContext.current

    val screenState by drawingViewModel.uiState.collectAsState()
    val drawingViewInstance by remember { mutableStateOf(DrawingView(context)) }
    val frameAnimatorViewInstance by remember { mutableStateOf(FrameAnimatorView(context)) }
    var sliderPosition by remember { mutableFloatStateOf(1f) }
    val coroutineScope = rememberCoroutineScope()
    var drawingViewCreated by remember { mutableStateOf(false) }


    val alpha by animateFloatAsState(
        targetValue = if (!screenState.isAnimating) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "Hide buttons when animating"
    )

    var showLayers by remember { mutableStateOf(false) }
    LaunchedEffect(screenState.selectedFrameIndex) {
        if (drawingViewCreated) {
            drawingViewModel.setSelectedFrameIndex(max(0, screenState.selectedFrameIndex))
            drawingViewInstance.clear()
            drawingViewInstance.setBackgroundBitmap(
                screenState.framesList.getOrNull(screenState.selectedFrameIndex - 1)?.bitmap
            )
            drawingViewInstance.setBitmap(screenState.framesList.getOrNull(screenState.selectedFrameIndex)?.bitmap)
        }
    }
    LaunchedEffect(screenState.pencilThickness, screenState.eraserThickness) {
        
        drawingViewInstance.pencilThickness = screenState.pencilThickness / 10
        drawingViewInstance.eraserThickness = screenState.eraserThickness / 10
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)

    ) {
        ListOfFramesDialog(showDialog = showLayers,
            onDismiss = { showLayers = false },
            frames = screenState.framesList,
            onFrameClick = {
                drawingViewModel.setSelectedFrameIndex(it)
            },
            onDeleteFrame = { index ->
                if (index in screenState.framesList.indices) {

                    drawingViewModel.setFramesList(screenState.framesList.toMutableList().apply {
                        removeAt(index)
                    })
                    if(index <= screenState.selectedFrameIndex){
                        drawingViewModel.setSelectedFrameIndex(screenState.selectedFrameIndex - 1)
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
                        drawingViewModel.setFramesList(screenState.framesList + framesToAdd)
                        drawingViewModel.setSelectedFrameIndex(screenState.framesList.size)

                    }
                }
            })

        ThicknessSlider(selectedThicknessSelector = screenState.selectedThicknessSelector,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .padding(horizontal = 20.dp)
                .zIndex(10f),
            pencilThickness = screenState.pencilThickness,
            eraserThickness = screenState.eraserThickness,
            onPenThicknessChange = { drawingViewModel.setPencilThickness(it) },
            onEraserThicknessChange = { drawingViewModel.setEraseThickness(it) })

        AnimatedVisibility(
            visible = screenState.isColorPickerOpened,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .zIndex(10f),
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PaletteView(screenState.isColorPickerExpanded) { color ->
                   drawingViewModel.setSelectedColor(color)
                    drawingViewInstance.currentColor = color.toArgb()
                    drawingViewModel.setIsColorPickerOpened(false)
                    drawingViewModel.setIsColorPickerExpanded(false)
                }
                Spacer(modifier = Modifier.height(12.dp))
                QuickPaletteView(onExpand = {
                    drawingViewModel.setIsColorPickerExpanded(!screenState.isColorPickerExpanded)

                }, onColorSelected = { color ->
                    drawingViewModel.setSelectedColor(color)
                    drawingViewInstance.currentColor = color.toArgb()
                    drawingViewModel.setIsColorPickerOpened(false)
                    drawingViewModel.setIsColorPickerExpanded(false)
                })
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                IconButton(
                    modifier = Modifier.alpha(alpha),
                    clickable = !screenState.isAnimating,
                    painter = painterResource(id = R.drawable.ic_arrow_back_active),
                    contentDescription = "Back"
                ) {
                    drawingViewInstance.undo()
                }
                Spacer(modifier = Modifier.width(Dimensions.iconsDistance))

                IconButton(
                    modifier = Modifier.alpha(alpha),
                    clickable = !screenState.isAnimating,
                    painter = painterResource(id = R.drawable.ic_arrow_forward_active),
                    contentDescription = "Back"
                ) {
                    drawingViewInstance.redo()
                }
                Spacer(modifier = Modifier.weight(1f))

                IconButton(modifier = Modifier.alpha(alpha),
                    clickable = !screenState.isAnimating,
                    painter = painterResource(id = R.drawable.ic_bin),
                    contentDescription = "Back",
                    onLongClick = {
                        drawingViewModel.setFramesList(emptyList())
                        drawingViewModel.setSelectedFrameIndex(0)
                    }) {
                    if (screenState.selectedFrameIndex in screenState.framesList.indices) {
                        drawingViewModel.setFramesList(screenState.framesList.toMutableList().apply {
                            removeAt(screenState.selectedFrameIndex)
                        })

                    }
                    drawingViewModel.setSelectedFrameIndex(screenState.selectedFrameIndex - 1)
                }
                Spacer(modifier = Modifier.width(Dimensions.iconsDistance))
                IconButton(
                    modifier = Modifier.alpha(alpha),
                    clickable = !screenState.isAnimating,
                    painter = painterResource(id = R.drawable.ic_file_plus),
                    contentDescription = "Back"
                ) {
                    drawingViewModel.setFramesList(screenState.framesList.toMutableList().apply {
                        add(screenState.selectedFrameIndex+1, Frame(Bitmap.createBitmap(drawingViewInstance.canvasWidth, drawingViewInstance.canvasHeight, Bitmap.Config.ARGB_8888)))
                    })
                    drawingViewModel.setSelectedFrameIndex(screenState.selectedFrameIndex + 1)
                }
                Spacer(modifier = Modifier.width(Dimensions.iconsDistance))
                IconButton(
                    modifier = Modifier.alpha(alpha),
                    clickable = !screenState.isAnimating,
                    painter = painterResource(id = R.drawable.ic_layers),
                    contentDescription = "Back"
                ) {
                    showLayers = true
                }
                Spacer(modifier = Modifier.width(Dimensions.iconsDistance))
                IconButton(
                    modifier = Modifier.alpha(alpha),
                    clickable = !screenState.isAnimating,
                    painter = painterResource(id = R.drawable.ic_copy),
                    contentDescription = "Back"
                ) {
                    if (screenState.selectedFrameIndex in screenState.framesList.indices) {
                        drawingViewModel.setFramesList(screenState.framesList.toMutableList().apply {
                            add(screenState.selectedFrameIndex, screenState.framesList[screenState.selectedFrameIndex])
                        })

                    }
                    drawingViewModel.setSelectedFrameIndex(screenState.selectedFrameIndex + 1)
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    painter = painterResource(id = R.drawable.ic_pause),
                    contentDescription = "Back"
                ) {
                    drawingViewModel.setAnimation(false)

                    frameAnimatorViewInstance.stopAnimation()

                }
                Spacer(modifier = Modifier.width(Dimensions.iconsDistance))
                IconButton(
                    painter = painterResource(id = R.drawable.ic_play),
                    contentDescription = "Back"
                ) {
                    drawingViewModel.setAnimation(true)
                    drawingViewModel.setIsColorPickerExpanded(false)
                    drawingViewModel.setIsColorPickerOpened(false)

                    frameAnimatorViewInstance.setFrames(screenState.framesList)
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
                    if (screenState.isAnimating) {
                        AndroidView(factory = {
                            frameAnimatorViewInstance
                        })
                    } else {
                        AndroidView(factory = {
                            drawingViewInstance.onDrawingFinishListener = {
                                drawingViewModel.setFramesList(screenState.framesList.toMutableList().apply {
                                    if (screenState.selectedFrameIndex in screenState.framesList.indices) set(
                                        screenState.selectedFrameIndex,
                                        it
                                    )
                                    else add(it)
                                })
                            }
                            drawingViewInstance.onCanvasLoaded = {
                                drawingViewCreated = true
                                if(screenState.framesList.isEmpty()){
                                    drawingViewModel.setFramesList(listOf(it))
                                }
                                drawingViewInstance.setBitmap(
                                    screenState.framesList.getOrNull(screenState.selectedFrameIndex)?.bitmap
                                )

                            }
                            drawingViewInstance
                        })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.height(50.dp)) {
                Row(modifier = Modifier.alpha(alpha)) {
                    PressingIconButton(modifier = Modifier,
                        painter = painterResource(id = R.drawable.ic_pencil),
                        contentDescription = "Back",
                        onClick = { drawingViewInstance.setEraserMode(false) },
                        onPressed = {
                            drawingViewModel.setSelectedThicknessSelector(Instruments.PENCIL)

                        },
                        onReleased = {
                            drawingViewModel.setSelectedThicknessSelector(null)

                        },
                        onMoved = { change ->
                            drawingViewModel.setPencilThickness(screenState.pencilThickness + change)


                        })
                    Spacer(modifier = Modifier.width(Dimensions.iconsDistance))

                    PressingIconButton(modifier = Modifier,
                        painter = painterResource(id = R.drawable.ic_eraser),
                        contentDescription = "Back",
                        onClick = { drawingViewInstance.setEraserMode(true) },
                        onPressed = {
                            drawingViewModel.setSelectedThicknessSelector(Instruments.ERASER)

                        },
                        onReleased = {
                            drawingViewModel.setSelectedThicknessSelector(null)

                        },
                        onMoved = { change ->
                            drawingViewModel.setEraseThickness(screenState.eraserThickness + change)


                        })
                    Spacer(modifier = Modifier.width(Dimensions.iconsDistance))

                    IconButton(
                        clickable = !screenState.isAnimating,
                        painter = painterResource(id = R.drawable.ic_instruments),
                        contentDescription = "Back"
                    ) {}

                    Spacer(modifier = Modifier.width(Dimensions.iconsDistance))
                    ColorPicker(screenState.selectedColor, clickable = !screenState.isAnimating) {
                        drawingViewModel.setIsColorPickerOpened(!screenState.isColorPickerOpened)
                        drawingViewModel.setIsColorPickerExpanded(false)
                    }

                    Spacer(modifier = Modifier.width(Dimensions.iconsDistance))
                    if (screenState.isGifLoading) {
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
                            drawingViewModel.setIsGifLoading(true)
                            coroutineScope.launch {
                                val gifData = withContext(Dispatchers.Default) {
                                    createGifByteArray(
                                        screenState.framesList.map {
                                            it.bitmap.copy(
                                                Bitmap.Config.ARGB_8888, false
                                            )
                                        }, delayMs = 100, loop = true
                                    )
                                }
                                drawingViewModel.setIsGifLoading(false)
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
                        value = sliderPosition, onValueChange = {
                            sliderPosition =
                                it; frameAnimatorViewInstance.animationSpeedFactor = it
                        }, colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.secondary,
                            activeTrackColor = MaterialTheme.colorScheme.secondary,
                            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                        ), valueRange = 0.1f..2f
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
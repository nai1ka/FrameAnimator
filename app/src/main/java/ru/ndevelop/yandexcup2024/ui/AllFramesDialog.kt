package ru.ndevelop.yandexcup2024.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import ru.ndevelop.yandexcup2024.models.Frame

@Composable
fun ListOfFramesDialog(
    showDialog: Boolean,
    onFrameClick: (Int) -> Unit,
    onDismiss: () -> Unit,
    frames: List<Frame>,
    onDeleteFrame: (Int) -> Unit,
    onCreateRandomFrames: (Int) -> Unit
) {
    var createRandomFrames by remember { mutableStateOf(false) }
    var selectedNumberOfRandomFrames by remember { mutableStateOf("1") }
    if (showDialog) {

        Dialog(onDismissRequest = {
            createRandomFrames = false
            selectedNumberOfRandomFrames = "1"
            onDismiss()
        }) {
            Surface(
                shape = MaterialTheme.shapes.medium, color = Color.White, modifier = Modifier
                    .padding(16.dp)
            ) {
                if (createRandomFrames) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Text("Введите количество кадров", color = Color.Black)
                        Text("Кадры будут созданы случайно", color = Color.Black)
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = selectedNumberOfRandomFrames,
                            onValueChange = { input ->
                                selectedNumberOfRandomFrames = input
                            },

                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(onClick = {
                            if (selectedNumberOfRandomFrames.isNotEmpty() && selectedNumberOfRandomFrames.isDigitsOnly() && selectedNumberOfRandomFrames.toInt() > 0) {
                                onCreateRandomFrames(selectedNumberOfRandomFrames.toInt())
                                createRandomFrames = false
                                selectedNumberOfRandomFrames = "1"
                                onDismiss()
                            }
                        }, modifier = Modifier.align(Alignment.End)) {
                            Text("Создать", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.widthIn(max = 240.dp)
                    ) {
                        LazyColumn(modifier = Modifier.heightIn(max = 500.dp)) {
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
                            color = Color.Blue,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
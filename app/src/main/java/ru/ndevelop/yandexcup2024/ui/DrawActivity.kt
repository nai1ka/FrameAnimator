package ru.ndevelop.yandexcup2024.ui


import android.os.Bundle
import androidx.activity.compose.setContent


class DrawActivity : androidx.activity.ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }
}



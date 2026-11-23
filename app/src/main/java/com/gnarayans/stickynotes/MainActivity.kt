package com.gnarayans.stickynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gnarayans.stickynotes.presentation.navigation.StickyNotesNavHost
import com.gnarayans.stickynotes.presentation.theme.StickyNotesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StickyNotesTheme {
                StickyNotesNavHost()
            }
        }
    }
}

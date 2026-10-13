package dev.danielkindl.cistula

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.danielkindl.cistula.ui.CistulaApp
import dev.danielkindl.cistula.ui.theme.CistulaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CistulaTheme {
                CistulaApp()
            }
        }
    }
}

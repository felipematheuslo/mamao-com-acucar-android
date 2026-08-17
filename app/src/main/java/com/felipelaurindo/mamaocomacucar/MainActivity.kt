package com.felipelaurindo.mamaocomacucar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.felipelaurindo.mamaocomacucar.ui.theme.MamaoComAcucarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MamaoComAcucarTheme {
                MamaoApp()
            }
        }
    }
}
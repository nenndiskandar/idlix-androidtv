package com.iskanflix.tv

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.iskanflix.tv.ui.navigation.AppNavigation
import com.iskanflix.tv.ui.theme.IskanFlixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Keep screen on while TV app is running
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            IskanFlixTheme {
                AppNavigation()
            }
        }
    }
}

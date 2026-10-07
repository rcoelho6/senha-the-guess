package com.senhadeguess.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.senhadeguess.mobile.ui.navigation.SenhaNavigation
import com.senhadeguess.mobile.ui.theme.SenhaAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SenhaAppTheme {
                SenhaNavigation()
            }
        }
    }
}

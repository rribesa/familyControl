package br.com.rribesa.familycontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import br.com.rribesa.familycontrol.core.ui.screen.SplashScreen
import br.com.rribesa.familycontrol.core.ui.theme.FamilyControlTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FamilyControlTheme {
                SplashScreen(
                    modifier = Modifier.fillMaxSize(),
                    onSplashFinished = {
                        // Navigate to Auth or Dashboard once those features are ready
                    }
                )
            }
        }
    }
}

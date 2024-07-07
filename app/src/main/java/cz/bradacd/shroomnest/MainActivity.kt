package cz.bradacd.shroomnest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cz.bradacd.shroomnest.apiclient.RetrofitInstance
import cz.bradacd.shroomnest.ui.navigation.AppNavigation
import cz.bradacd.shroomnest.ui.theme.ShroomNestTheme
import cz.bradacd.shroomnest.viewmodel.getSettings

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShroomNestTheme {
                App()
            }
        }
    }
}


@Composable
fun App() {
    val context = LocalContext.current
    RetrofitInstance.init(getSettings(context).apiRoot)

    Box {
        AppNavigation()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ShroomNestTheme {
        App()
    }
}
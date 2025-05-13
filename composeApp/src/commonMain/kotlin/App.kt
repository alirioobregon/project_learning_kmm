import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import screens.MainScreen

@Composable
fun App() {
    Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
//        AnimateImage()
        Navigator(screen = MainScreen()) { navigator ->
//            ScaleTransition(navigator)
            SlideTransition(navigator)
//            FadeTransition(navigator)
        }
    }
}





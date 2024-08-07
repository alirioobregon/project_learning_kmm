package screens

import Greeting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import presentation.MainViewModel

class MainScreen : Screen {
    @Composable
    override fun Content() {
        MaterialTheme {
            val viewModel = rememberScreenModel { MainViewModel() }
            ViewMain(viewModel)
        }
    }

    @Composable
    fun ViewMain(viewModel: MainViewModel) {
        val greeting = Greeting()
        val uiState by viewModel.uiState.collectAsState()

        BoxWithConstraints() {
            Column(Modifier.background(Color.White).fillMaxHeight()) {
                Text(greeting.greet(), Modifier.fillMaxWidth(), color = Color.Black)
                Text(greeting.getIp(), Modifier.fillMaxWidth(), color = Color.Black)
                Row {
                    if (!uiState.connected) {
                        OutlinedButton(onClick = {
                            viewModel.startSocket()
                        }) {
                            Text("Start Server")
                        }
                    }
                    OutlinedButton(onClick = {
                        viewModel.connectToServer()
                    }) {
                        Text("Connect to server")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AnimateImage() {
    val navigator = LocalNavigator.current
    MaterialTheme {
        var greetingText by remember { mutableStateOf("Hello World!") }
        var showImage by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {
                    navigator?.pop()
                }
            ) {
                Text("Volver atras")
            }
            Button(onClick = {
                greetingText = "Compose: ${Greeting().greet()}"
                showImage = !showImage
            }) {
                Text(greetingText)
            }
            AnimatedVisibility(showImage) {
                Image(
                    painterResource("compose-multiplatform.xml"),
                    null
                )
            }
        }
    }
}




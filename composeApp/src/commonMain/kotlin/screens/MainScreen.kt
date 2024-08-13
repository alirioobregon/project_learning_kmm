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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
        var txtIp by rememberSaveable { mutableStateOf("192.168.100.") }
        var msgToSend by rememberSaveable { mutableStateOf("") }

        BoxWithConstraints() {
            Column(Modifier.background(Color.White).fillMaxHeight()) {
                Text(greeting.greet(), Modifier.fillMaxWidth(), color = Color.Black)
                Text("Tu ip es: ${greeting.getIp()}", Modifier.fillMaxWidth(), color = Color.Black)
                Row {
                    OutlinedButton(
                        onClick = {
                            viewModel.startSocket(greeting.getIp())
                        }, enabled = !uiState.connected, colors = ButtonDefaults.buttonColors(
                            containerColor = if (!uiState.connected) Color.White else Color.Green,
                            disabledContainerColor = if (!uiState.connected) Color.White else Color.Green
                        )
                    ) {
                        Text("Start Server", color = Color.Black)
                    }

                }

                if (!uiState.isServer) {
                    OutlinedTextField(
                        value = txtIp,
                        enabled = !uiState.connected,
                        onValueChange = {
                            txtIp = it
//                        usernameError = it.trim().isEmpty()
                        },
                        singleLine = true,
                        label = { Text("ip") },
//                    isError = usernameError,
                        leadingIcon = {
                            Icon(
                                Icons.Default.AccountCircle,
                                contentDescription = null
                            )
                        },
//                    supportingText = { Text(if (usernameError) "Este campo es requerido" else "") },
                        trailingIcon = {
                            if (txtIp.trim().isNotEmpty()) {
                                IconButton(onClick = { txtIp = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = null)
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .semantics {
//                            if (usernameError) error("Este campo es requerido")
                            }
                    )

                    if (!uiState.connected) {
                        OutlinedButton(onClick = {
                            viewModel.connectToServer(txtIp)
                        }, enabled = txtIp.isNotEmpty()) {
                            Text("Connect to server")
                        }
                    }
                }

                OutlinedTextField(
                    value = msgToSend,
                    onValueChange = {
                        msgToSend = it
//                        usernameError = it.trim().isEmpty()
                    },
                    singleLine = true,
                    label = { Text("escribe") },
//                    isError = usernameError,
                    leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
//                    supportingText = { Text(if (usernameError) "Este campo es requerido" else "") },
                    trailingIcon = {
                        if (msgToSend.trim().isNotEmpty()) {
                            IconButton(onClick = { msgToSend = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .semantics {
//                            if (usernameError) error("Este campo es requerido")
                        }
                )

                OutlinedButton(onClick = {
                    viewModel.sendMessage(msgToSend)
                }, enabled = msgToSend.isNotEmpty()) {
                    Text("Send message")
                }

                LazyColumn(Modifier.fillMaxWidth()) {
//                    val list = listOf("saasasas", "asasaasa")
                    items(uiState.listMessage) {
                        val align = if (it.second == 1) TextAlign.Start else TextAlign.End
                        val background = if (it.second == 2) Color.LightGray else Color.Transparent
                        Text(
                            text = "${it.first}",
                            textAlign = align,
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth()
                                .background(color = background)
                        )
//                        Text(text = it, textAlign = TextAlign.Start, color = Color.Black)
                    }
                }

            }
        }
    }
}

//@OptIn(ExperimentalResourceApi::class)
//@Composable
//fun AnimateImage() {
//    val navigator = LocalNavigator.current
//    MaterialTheme {
//        var greetingText by remember { mutableStateOf("Hello World!") }
//        var showImage by remember { mutableStateOf(false) }
//        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//            Button(
//                onClick = {
//                    navigator?.pop()
//                }
//            ) {
//                Text("Volver atras")
//            }
//            Button(onClick = {
//                greetingText = "Compose: ${Greeting().greet()}"
//                showImage = !showImage
//            }) {
//                Text(greetingText)
//            }
//            AnimatedVisibility(showImage) {
//                Image(
//                    painterResource("compose-multiplatform.xml"),
//                    null
//                )
//            }
//        }
//    }
//}




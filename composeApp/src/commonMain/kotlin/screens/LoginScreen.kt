package screens

import Greeting
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

class LoginScreen: Screen {
    @Composable
    override fun Content() {
        Login()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Login() {
    val navigator = LocalNavigator.current
    var username by remember { mutableStateOf("Ali") }
    var password by remember { mutableStateOf("1234") }
    var statusSnackBar by remember { mutableStateOf(false) }
    val textTitle by remember { mutableStateOf("Compose: ${Greeting().greet()}") }
    var usernameError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordHidden by rememberSaveable { mutableStateOf(false) }

    Surface(color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = textTitle,
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    usernameError = it.trim().isEmpty()
                },
                singleLine = true,
                label = { Text(if (usernameError) "UserName*" else "UserName") },
                isError = usernameError,
                leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                supportingText = { Text(if (usernameError) "Este campo es requerido" else "") },
                trailingIcon = {
                    if (username.trim().isNotEmpty()) {
                        IconButton(onClick = { username = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .semantics {
                        if (usernameError) error("Este campo es requerido")
                    }
            )
//            if (usernameError) {
//                ProvideTextStyle(value = MaterialTheme.typography.bodySmall.copy(color = Color.Red)) {
//                    Text(text = "Este campo es requerido", modifier = Modifier.align(Alignment.Start).padding(start = 8.dp))
//                }
//            }
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = it.trim().isEmpty()
                },
                label = { Text(text = "Password*") },
                isError = passwordError,
                supportingText = { Text(if (passwordError) "Este campo es requerido" else "") },
                visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordHidden = !passwordHidden }) {
                        val visibilityIcon =
                            if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        Icon(imageVector = visibilityIcon, contentDescription = null)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
            Button(
                onClick = {
                    // Aquí iría la lógica para autenticar al usuario
                    // Se podría validar el nombre de usuario y contraseña ingresados
                    // y proceder según el resultado
                    statusSnackBar = true
                    if (username.trim().isEmpty()) {
                        usernameError = true
                    } else if (password.trim().isEmpty()) {
                        passwordError = true
                    } else {
                        navigator?.push(MainScreen())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Login")
                AnimatedVisibility(statusSnackBar) {

                }
            }
        }
    }
}
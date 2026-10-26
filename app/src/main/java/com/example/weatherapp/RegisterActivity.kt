package com.example.weatherapp

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.example.weatherapp.db.fb.FBDatabase
import com.example.weatherapp.db.fb.toFBUser
import com.example.weatherapp.model.User

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RegisterPage(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    WeatherAppTheme {
//        Greeting("Android")
//    }
//}

@Composable
fun RegisterPage(modifier: Modifier = Modifier) {
    var nomeUser by rememberSaveable { mutableStateOf(value = "")}
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var repeatPassword by rememberSaveable { mutableStateOf(value = "")}
    val activity = LocalActivity.current as Activity
    Column(
        modifier = modifier
            .padding(12.dp)
            .fillMaxSize(),
        //verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        val fieldModifier = modifier.fillMaxWidth(fraction = 0.9f)
        Text(
            text = "Registre sua conta.",
            fontSize = 24.sp
        )

        //Spacer(modifier = modifier.size(3.dp))

        OutlinedTextField(
            value = nomeUser,
            label = { Text(text = "Digite seu nome") },
            modifier = fieldModifier,
            onValueChange = { nomeUser = it }
        )

        //Spacer(modifier = modifier.size(3.dp))

        OutlinedTextField(
            value = email,
            label = { Text(text = "Digite seu e-mail") },
            modifier = fieldModifier,
            onValueChange = { email = it }
        )

        //Spacer(modifier = modifier.size(3.dp))

        OutlinedTextField(
            value = password,
            label = { Text(text = "Digite sua senha") },
            modifier = fieldModifier,
            onValueChange = { password = it },
            visualTransformation = PasswordVisualTransformation()
        )

        //Spacer(modifier = modifier.size(3.dp))

        OutlinedTextField(
            value = repeatPassword,
            label = { Text(text = "Repita sua senha") },
            modifier = fieldModifier,
            onValueChange = { repeatPassword = it },
            visualTransformation = PasswordVisualTransformation()
        )

        //Spacer(modifier = modifier.size(3.dp))

        Row(/*modifier = modifier.padding(6.dp).fillMaxWidth()*/
        horizontalArrangement = Arrangement.Center) {
            Button( onClick = {
                Firebase.auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(activity) { task ->
                        if (task.isSuccessful) {
                            FBDatabase().register(User(nomeUser, email).toFBUser())

                            Toast.makeText(activity, "Registro OK!", Toast.LENGTH_LONG).show()
                            activity.finish()
                        } else {
                            Toast.makeText(activity, "Registro FALHOU!", Toast.LENGTH_LONG).show()
                        }
                    }
            },
                enabled = nomeUser.isNotEmpty() &&
                        email.isNotEmpty() &&
                        password.isNotEmpty() &&
                        repeatPassword.isNotEmpty() &&
                        password == repeatPassword
            ) {
                Text("Registrar")
            }

            Spacer(modifier = Modifier.size(12.dp))

            Button(
                onClick = { nomeUser = ""; email = ""; password = ""; repeatPassword = "" },
                //enabled = email.isNotEmpty() && password.isNotEmpty()
            ) {
                Text("Limpar")
            }
        }
    }

}
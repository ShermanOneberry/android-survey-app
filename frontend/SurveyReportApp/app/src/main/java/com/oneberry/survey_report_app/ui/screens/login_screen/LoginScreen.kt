package com.oneberry.survey_report_app.ui.screens.login_screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oneberry.survey_report_app.R

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel =
        viewModel(factory = LoginViewModel.Factory),
    popBackStack: () -> Boolean,
    popUpToForm: () -> Unit,
    contentPadding: PaddingValues,
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val context = LocalContext.current
    val uiState = loginViewModel.uiState.collectAsState().value
    LaunchedEffect(Unit) {
        loginViewModel
            .toastMessage
            .collect { message ->
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_SHORT,
                ).show()
            }
    }
    LaunchedEffect(Unit) {//TODO: Check if this is the correct method
        loginViewModel
            .uiState
            .collect { state ->
                if (state.successfulLogin) {
                    popUpToForm()
                }
            }
    }

    Column(
        modifier = Modifier
            .padding(contentPadding) //Margin
            .verticalScroll(rememberScrollState())
            .padding(mediumPadding), //Padding
        verticalArrangement = Arrangement.spacedBy(
            space = mediumPadding,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(
            text = stringResource(R.string.login),
            style = MaterialTheme.typography.titleLarge,
        )

        TextField( //TODO: Replace these with text box template
            label = { Text(text = "Username") },
            value = uiState.username,
            enabled = uiState.uiEnabled,
            onValueChange = { loginViewModel.updateUsername(it) })

        TextField(
            label = { Text(text = "Password") },
            value = uiState.password,
            enabled = uiState.uiEnabled,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { loginViewModel.updatePassword(it) })

        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = { loginViewModel.attemptLogin() },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Login")
            }
        }
    }
}
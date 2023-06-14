package com.oneberry.survey_report_app.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Preview
@Composable
fun TextInputPreview(){
    var inputText by remember { mutableStateOf("") }
    TextInputTemplate(
        fieldTitle = "Field Name",
        fieldInput = inputText,
        isFieldValid = (inputText == "Valid"),
        isFinalInput = true,
        onInputChange = {inputText = it},
        errorMessage = if (inputText == "Valid") null else "Text must be 'Valid'"
    )
}
@Composable
fun TextInputTemplate(
    fieldTitle: String,
    fieldInput: String,
    isFieldValid: Boolean,
    errorMessage: String?,
    onInputChange: (String) -> Unit,
    isFinalInput: Boolean = false,
    isMultiLine: Boolean = false,
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = fieldInput,
        singleLine = !isMultiLine,
        shape = MaterialTheme.shapes.large,
        modifier =
        if (isMultiLine)
            Modifier
                .fillMaxWidth()
                .heightIn(1.dp, Dp.Infinity)
        else Modifier.fillMaxWidth(),
        //colors = TextFieldDefaults.textFieldColors(containerColor = colorScheme.surface),
        onValueChange = onInputChange,
        label = { Text(fieldTitle) },
        isError = !isFieldValid,
        supportingText = if (errorMessage == null) null else { { Text(errorMessage) } },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = if (isFinalInput) ImeAction.Done else ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onDone = { focusManager.clearFocus() },
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )
    )
}
package com.oneberry.survey_report_app.ui.screens.preview_screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oneberry.survey_report_app.R
import com.oneberry.survey_report_app.ui.screens.survey_report_screen.SurveyReportNavRequest

@Composable
fun PreviewScreen(
    previewViewModel: PreviewViewModel = viewModel(factory = PreviewViewModel.Factory),
    popBackStack: () -> Boolean,
    contentPadding: PaddingValues,
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        previewViewModel
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
        previewViewModel
            .navRequest
            .collect { request ->
                when(request) {
                    PreviewNavRequest.Back -> {
                        popBackStack()
                    }
                    is PreviewNavRequest.ReLogin -> {
                        //TODO
                    }
                }
            }
    }
    Column(
        modifier = Modifier
            .padding(contentPadding) //Margin
            .verticalScroll(rememberScrollState())
            .padding(mediumPadding) //Padding
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            space = mediumPadding,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        //TODO: Generate preview here
        Button(
            onClick = { previewViewModel.triggerSubmission() }
        ) {
            Text(text = "Submit")
        }
    }
}
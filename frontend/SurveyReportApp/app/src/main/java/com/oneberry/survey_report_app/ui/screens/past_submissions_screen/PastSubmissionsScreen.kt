package com.oneberry.survey_report_app.ui.screens.past_submissions_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oneberry.survey_report_app.R

@Composable
fun PastSubmissionsScreen(
    pastSubmissionsViewModel: PastSubmissionsViewModel =
        viewModel(factory = PastSubmissionsViewModel.Factory),
    popBackStack: () -> Boolean,
    navigateToLogin: (String) -> Unit,
    contentPadding: PaddingValues,
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    LaunchedEffect(Unit) {
        pastSubmissionsViewModel
            .navRequest
            .collect { request ->
                when(request) {
                    PastSubmissionsNavRequest.Back -> {
                        popBackStack()
                    }
                    PastSubmissionsNavRequest.Login -> {
                        navigateToLogin("")
                    }
                    is PastSubmissionsNavRequest.ReLogin -> {
                        navigateToLogin(request.username)
                    }
                }
            }
    }
    //Not using 'by' to allow for smart cast
    val pastSubmissions = pastSubmissionsViewModel.pastSubmissions.collectAsState().value
    if (pastSubmissions == null || pastSubmissions.totalItems == 0 ) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = if (pastSubmissions == null) "Loading past submissions..."
                    else "No past submissions found...",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
        //TODO: Maybe consider adding a 'proper' loading/empty screen instead?
        return //Suppress rendering
    }
    LazyColumn(
        modifier = Modifier
            .padding(contentPadding) //Margin
            .verticalScroll(rememberScrollState())
            .padding(mediumPadding), //Padding
        verticalArrangement = Arrangement.spacedBy(
            space = mediumPadding,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        items(pastSubmissions.items) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Submission ${it.batchID} for Batch ${it.batchNumber}")
                //TODO: Add preview, edit button
            }
        }
    }
}
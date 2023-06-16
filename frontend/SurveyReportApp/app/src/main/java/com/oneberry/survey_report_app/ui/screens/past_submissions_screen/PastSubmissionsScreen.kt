package com.oneberry.survey_report_app.ui.screens.past_submissions_screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oneberry.survey_report_app.R
import com.oneberry.survey_report_app.ui.composables.TextInputTemplate
import kotlinx.coroutines.launch

@Composable
fun PastSubmissionsScreen(
    pastSubmissionsViewModel: PastSubmissionsViewModel =
        viewModel(factory = PastSubmissionsViewModel.Factory),
    popBackStack: () -> Boolean,
    navigateToLogin: (String) -> Unit,
    contentPadding: PaddingValues,
    navigateToPreview: () -> Unit,
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        pastSubmissionsViewModel
            .toastMessage
            .collect { message ->
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_SHORT,
                ).show()
            }
    }
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
                    PastSubmissionsNavRequest.View -> {
                        navigateToPreview()
                    }
                }
            }
    }
    //Not using 'by' to allow for smart cast
    val pastSubmissions = pastSubmissionsViewModel.pastSubmissions.collectAsState().value
    val apiState = pastSubmissions.apiState
    val searchBox = pastSubmissions.searchBox

    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier
            .padding(contentPadding) //Margin
            .padding(mediumPadding), //Padding
        verticalArrangement = Arrangement.spacedBy(
            space = mediumPadding,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = scrollState
    ){
        item {
            Text("Filter by Address")
            TextInputTemplate(
                fieldTitle = "Block number",
                fieldInput = searchBox.block,
                isFieldValid = true,
                onInputChange = { pastSubmissionsViewModel.updateBlockFilter(it) },
                errorMessage = null,
            )
            TextInputTemplate(
                fieldTitle = "Street",
                fieldInput = searchBox.street,
                isFieldValid = true,
                onInputChange = { pastSubmissionsViewModel.updateStreetFilter(it) },
                errorMessage = null,
            )
            Button(onClick = { pastSubmissionsViewModel.triggerListWithNewFilter()}) {
                Text("Search with new filter")
            }
            val filterText = StringBuilder()
            if (searchBox.blockFilter.isNotBlank()) {
                filterText.append("Block '${searchBox.blockFilter}'")
                if (searchBox.streetFilter.isNotBlank()) {
                    filterText.append("; ")
                }
            }
            if (searchBox.streetFilter.isNotBlank()) {
                filterText.append("Street '${searchBox.streetFilter}'")
            }
            if (filterText.isEmpty()) {
                filterText.append("None")
            }
            filterText.insert(0,"Current Filter: ")

            Text(filterText.toString())
        }
        if (apiState == null || apiState.totalItems == 0 ) {
            item{
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = if (apiState == null) "Loading past submissions..."
                        else "No past submissions found...",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
            //TODO: Maybe consider adding a 'proper' loading/empty screen instead?
            return@LazyColumn //Suppress rendering
        }
        items(apiState.items) {
            Divider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val surveyReq = it.item.expand.surveyRequest
                Text(
                    text = "Batch ${surveyReq.batchNumber}; Report ${surveyReq.batchID}\n" +
                        "Blk ${surveyReq.block}, ${surveyReq.streetName}",
                    modifier = Modifier.weight(1f))
                Spacer(Modifier.size(mediumPadding))
                Button(
                    onClick = {pastSubmissionsViewModel.viewSubmission(it.item)}
                ){
                    Text("View")
                }
                Spacer(Modifier.size(mediumPadding))
                Button(
                    onClick = {pastSubmissionsViewModel.editSubmission(it.item)},
                    enabled = it.sameUser &&
                            surveyReq.batchNumber == apiState.latestBatchNumber
                ){
                    Text("Edit")
                }
            }
        }
        item {
            Divider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    space = mediumPadding,
                    alignment = Alignment.CenterHorizontally
                ),
            ) {
                if (apiState.page > 1 ) {
                    Button(onClick = {
                        pastSubmissionsViewModel.attemptGetPrevPage()
                        coroutineScope.launch {
                            scrollState.animateScrollToItem(0)
                        }
                    }) {
                        Text("Previous")
                    }
                }
                Text("Page ${apiState.page}/${apiState.totalPages}")
                if (apiState.page < apiState.totalPages) {
                    Button(onClick = {
                        pastSubmissionsViewModel.attemptGetNextPage()
                        coroutineScope.launch {
                            scrollState.animateScrollToItem(0)
                        }
                    }) {
                        Text("Next")
                    }
                }
            }
        }
    }
}
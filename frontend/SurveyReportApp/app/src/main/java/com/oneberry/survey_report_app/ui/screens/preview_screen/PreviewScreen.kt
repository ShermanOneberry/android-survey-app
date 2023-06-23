package com.oneberry.survey_report_app.ui.screens.preview_screen

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oneberry.survey_report_app.R
import com.oneberry.survey_report_app.data.GroundType
import com.oneberry.survey_report_app.data.LocationType
import com.oneberry.survey_report_app.data.StoredImage
import java.io.File

@Composable
fun PreviewScreen(
    previewViewModel: PreviewViewModel = viewModel(factory = PreviewViewModel.Factory),
    navigateToLogin: (String) -> Unit,
    popBackStack: () -> Boolean,
    contentPadding: PaddingValues,
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val context = LocalContext.current
    val submittableReport = previewViewModel.surveyState.collectAsState().value
    val viewOnlyReport = previewViewModel.viewOnlySurveyState.collectAsState().value
    val report = viewOnlyReport ?: submittableReport
    val isViewOnly = viewOnlyReport != null
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
    LaunchedEffect(Unit) {
        previewViewModel
            .navRequest
            .collect { request ->
                when(request) {
                    PreviewNavRequest.Back -> {
                        popBackStack()
                    }
                    PreviewNavRequest.Login -> {
                        navigateToLogin("")
                    }
                    is PreviewNavRequest.ReLogin -> {
                        navigateToLogin(request.username)
                    }
                }
            }
    }
    LaunchedEffect(Unit) {
        previewViewModel.attemptLoadViewOnlySurvey()
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
        val loadingComplete = previewViewModel.viewOnlySurveyLoadedState.collectAsState().value
        if (!loadingComplete) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Loading...",
                    style = typography.titleLarge,
                )
            }
            //TODO: Maybe consider adding a 'proper' loading screen instead?
            return //Suppress rendering
        }
        Text(
            text = if (isViewOnly) "Review" else "Preview",
            style = typography.titleLarge,
        )
        Divider()
        Text(
            "Batch no. ${report.batchNum}; " +
                    "Survey ${report.intraBatchId}"
        )
        Text(
            "Date: ${report.surveyDate}, Time: ${report.surveyTime}"
        )
        Divider()
        Text(
            "Feasible: ${if (report.isFeasible) "Yes" else "No"}"
        )
        ImageDisplay("Survey Image",
            report.reasonImage,
            report.editOnlyData?.reasonImage
        )
        Divider()
        val nearbyLocationText =
            if (report.nearbyDescription.isBlank()) ""
            else " " + report.nearbyDescription.trim()
        val distanceNumber =
            report.locationDistance.substring(0, report.locationDistance.length - 1).trim()
        val generalLocationDescription =
            "${report.blockLocation.trim()} ${report.streetLocation.trim()}" +
            "${nearbyLocationText}. Distance: $distanceNumber meters away"
        if (report.isFeasible) {
            Text("Camera count: ${report.cameraCount}; Box count: ${report.boxCount}")
            val locationText = when(report.locationType) {
                LocationType.CORRIDOR ->
                    "Deploy at level ${report.corridorLevel.trim()} " +
                            "common corridor of $generalLocationDescription"
                LocationType.STAIRWAY -> {
                    val lowerLevel: String = report.stairwayLowerLevel.trim()
                    val upperLevel: String = (lowerLevel.toInt() + 1).toString()
                    "Deploy at staircase landing between " +
                            "level $lowerLevel and $upperLevel of $generalLocationDescription"
                }
                LocationType.GROUND -> {
                    val groundTypeFragment = when (report.groundType) {
                        GroundType.VOID_DECK -> "void deck "
                        GroundType.GRASS_PATCH -> "grass patch "
                        GroundType.OTHER -> ""
                    }
                    "Deploy at ground level $groundTypeFragment" +
                            "of $generalLocationDescription"
                }
                LocationType.MULTISTORYCARPARK -> //TODO: Check this format
                    "Deploy at MSCP level ${report.carparkLevel} of $generalLocationDescription"
                LocationType.ROOF ->
                    "Deploy at roof of $generalLocationDescription"
            }
            Text(locationText)
        } else {
            Text("Reason for infeasibility: ${report.nonFeasibleExplanation}")
        }
        if (report.hasAdditionalNotes) {
            Divider()
            Text(report.techniciansNotes)
            ImageDisplay(
                "Additional Image",
                report.extraImage,
                report.editOnlyData?.extraImage
            )
        }
        Divider()
        if (isViewOnly) {
            Button(
                onClick = { popBackStack() }
            ) {
                Text("Back to Past Submissions")
            }
        } else {
            Button(
                onClick = { previewViewModel.triggerSubmission() }
            ) {
                Text(text = "Submit")
            }
        }
    }
}
@Composable
fun ImageDisplay(imageCategory: String, image: File?, storedImage: StoredImage?){
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    Column(
        modifier = Modifier
            .padding(mediumPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(imageCategory)
        image?.let {
            val source = ImageDecoder.createSource(it)
            Image(bitmap = ImageDecoder.decodeBitmap(source).asImageBitmap(),
                contentDescription =null,
                modifier = Modifier.size(400.dp))
            Text(image.name)
        } ?: storedImage?.let {
            Image(bitmap = storedImage.bitmap.asImageBitmap(),
                contentDescription =null,
                modifier = Modifier.size(400.dp))
            Text(storedImage.filename)
        }
    }
}
package com.oneberry.survey_report_app.ui.screens.preview_screen

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    val report = previewViewModel.surveyState.collectAsState().value
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
                    is PreviewNavRequest.ReLogin -> {
                        navigateToLogin(request.username)
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
    ) {//TODO: Check preview
        Text(
            text = "Preview",
            style = typography.titleLarge,
        )
        Divider()
        Text(
            "Batch no. ${report.batchNum}; " +
                    "Survey ${report.intraBatchId}"
        )
        Divider()
        Text(
            "Feasible: ${if (report.isFeasible) "Yes" else "No"}"
        )
        ImageDisplay("Survey Image", report.reasonImage)
        Divider()
        val nearbyLocationText =
            if (report.nearbyDescription.isBlank())
                report.nearbyDescription.trim()
            else ""
        val distanceNumber =
            report.locationDistance.substring(0, report.locationDistance.length - 1).trim()
        val generalLocationDescription
        = "${report.blockLocation.trim()} ${report.streetLocation.trim()}" +
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
            ImageDisplay("Additional Image", report.extraImage)
        }
        Divider()
        Button(
            onClick = { previewViewModel.triggerSubmission() }
        ) {
            Text(text = "Submit")
        }
    }
}
@Composable
fun ImageDisplay(imageCategory: String, image: File?){
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val bitmap =  remember {
        mutableStateOf<Bitmap?>(null)
    }
    Column(
        modifier = Modifier
            .padding(mediumPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(imageCategory)
        image?.let {
            val source = ImageDecoder
                .createSource(it)
            bitmap.value = ImageDecoder.decodeBitmap(source)

            bitmap.value?.let {  btm ->
                Image(bitmap = btm.asImageBitmap(),
                    contentDescription =null,
                    modifier = Modifier.size(400.dp))
            }
            Text(image.name)
        }
    }
}
package com.oneberry.survey_report_app.ui.screens.survey_report_screen

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oneberry.survey_report_app.R
import com.oneberry.survey_report_app.data.GroundType
import com.oneberry.survey_report_app.data.LocationType
import com.oneberry.survey_report_app.ui.composables.TextInputTemplate
import com.oneberry.survey_report_app.util.getFile
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title
import java.io.File
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun SurveyReportScreen(
    surveyReportViewModel: SurveyReportViewModel =
        viewModel(factory = SurveyReportViewModel.Factory),
    navigateToLogin: (String) -> Unit,
    navigateToPreview: () -> Unit,
    navigateToPastSubmissions: () -> Unit,
    contentPadding: PaddingValues,
) {
    val surveyReportUiState by surveyReportViewModel.surveyState.collectAsState()
    val credentials by surveyReportViewModel.credentialLiveData.observeAsState()
    val isFeasible = surveyReportUiState.isFeasible
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        surveyReportViewModel
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
        surveyReportViewModel
            .navRequest
            .collect { request ->
                when(request) {
                    SurveyReportNavRequest.Login -> {
                        navigateToLogin("")
                    }
                    SurveyReportNavRequest.Preview -> {
                        navigateToPreview()
                    }
                    is SurveyReportNavRequest.ReLogin -> {
                        navigateToLogin(request.username)
                    }
                }
            }
    }
    val appHasLoaded by surveyReportViewModel.appHasLoaded.collectAsState()
    LaunchedEffect(credentials) {
        if (appHasLoaded) {
            return@LaunchedEffect
        }
        if (credentials == null) {
            return@LaunchedEffect //Still checking for credentials
        }
        if (credentials?.tryGetNotNullCredentials() != null) {
            surveyReportViewModel.setAppBootAsLoaded()
            return@LaunchedEffect
        }
        val possibleUsername = credentials?.username
        if (possibleUsername != null) {
            navigateToLogin(possibleUsername)
        } else {
            navigateToLogin("")
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
        if (!appHasLoaded) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            //TODO: Maybe consider adding a 'proper' loading screen instead?
            return //Suppress rendering
        }

        val displayUser = credentials?.username
        if(displayUser != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = displayUser)
                Button(onClick = { navigateToLogin("") }) {
                    Text("Change User")
                }
                Button(onClick = { surveyReportViewModel.logOut()}) {
                    Text("Logout")
                }
            }

            Divider()

            Button(onClick = { navigateToPastSubmissions() }) {
                Text("View previous submissions")
            }

        } else {
            Button(onClick = { navigateToLogin("") }) {
                Text("Login")
            }
        }

        Divider()
        val resetDialogState = rememberMaterialDialogState()
        MaterialDialog(
            dialogState = resetDialogState,
            buttons = {
                positiveButton("Ok") {
                    surveyReportViewModel.resetForm()
                }
                negativeButton("Cancel")
            }) {
            title(text = "Clear Form?")
            message("Do you wish to clear this form? This cannot be undone.")

        }
        if (surveyReportUiState.editOnlyData == null) {
            TextInputTemplate(
                fieldTitle = "Batch number",
                fieldInput = surveyReportUiState.batchNum,
                isFieldValid = surveyReportUiState.batchNumValid(),
                onInputChange = { surveyReportViewModel.updateBatchNum(it) },
                errorMessage = surveyReportUiState.batchNumError(),
            )
            TextInputTemplate(
                fieldTitle = "Survey number for batch",
                fieldInput = surveyReportUiState.intraBatchId,
                isFieldValid = surveyReportUiState.intraBatchIdValid(),
                onInputChange = { surveyReportViewModel.updateIntraBatchId(it) },
                errorMessage = surveyReportUiState.intraBatchIdError(),
            )

            Button(
                onClick = { resetDialogState.show() }
            ) {
                Text("Reset form")
            }
        } else {
            Text("Editing Survey ${surveyReportUiState.intraBatchId} " +
                    "for Batch ${surveyReportUiState.batchNum}")
            Button(
                onClick = { resetDialogState.show() }
            ) {
                Text("Create new survey report instead")
            }
        }

        Divider()
        Text("Survey DateTime")
        DateTimePicker(
            surveyReportUiState.surveyDate,
            { surveyReportViewModel.updateSurveyDate(it) },
            surveyReportUiState.surveyTime,
            { surveyReportViewModel.updateSurveyTime(it) }
        )
        Divider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text =
                "Is Feasible: "
                + if (isFeasible) "Yes" else "No")
            Switch(
                checked = isFeasible,
                onCheckedChange = { surveyReportViewModel.updateIsFeasible(it) },
            )
        }
        ImagePicker(
            "Reason Image for " + if (isFeasible) "Feasibility" else "Infeasibility",
            surveyReportUiState.reasonImage,
            surveyReportUiState.editOnlyData?.reasonImage?.bitmap,){
            surveyReportViewModel.updateReasonImage(it)
        }

        Divider()

        if (isFeasible) {
            TextInputTemplate(
                fieldTitle = "Distance to Unit",
                fieldInput = surveyReportUiState.locationDistance,
                isFieldValid = surveyReportUiState.locationDistanceValid(),
                onInputChange = { surveyReportViewModel.updateLocationDistance(it) },
                errorMessage = surveyReportUiState.locationDistanceError(),
            )
            TextInputTemplate(
                fieldTitle = "Camera Count",
                fieldInput = surveyReportUiState.cameraCount,
                isFieldValid = surveyReportUiState.cameraCountValid(),
                onInputChange = { surveyReportViewModel.updateCameraCount(it) },
                errorMessage = surveyReportUiState.cameraCountError()
            )
            TextInputTemplate(
                fieldTitle = "Box Count",
                fieldInput = surveyReportUiState.boxCount,
                isFieldValid = surveyReportUiState.boxCountValid(),
                onInputChange = { surveyReportViewModel.updateBoxCount(it) },
                errorMessage = surveyReportUiState.boxCountError()
            )

            Divider()

            LocationMenuSelection(surveyReportUiState.locationType) {
                surveyReportViewModel.updateLocationType(it)
            }
            when (surveyReportUiState.locationType) {
                LocationType.CORRIDOR -> {
                    TextInputTemplate(
                        fieldTitle = "Corridor Level",
                        fieldInput = surveyReportUiState.corridorLevel,
                        isFieldValid = surveyReportUiState.corridorLevelValid(),
                        onInputChange = { surveyReportViewModel.updateCorridorLevel(it) },
                        errorMessage = surveyReportUiState.corridorLevelError()
                    )
                }

                LocationType.STAIRWAY -> {
                    TextInputTemplate(
                        fieldTitle = "Lower Level",
                        fieldInput = surveyReportUiState.stairwayLowerLevel,
                        isFieldValid = surveyReportUiState.stairwayLowerLevelValid(),
                        onInputChange = { surveyReportViewModel.updateStairwayLowerLevel(it) },
                        errorMessage = surveyReportUiState.stairwayLowerLevelError()
                    )
                    val stairwayHelpText: String =
                        if (surveyReportUiState.stairwayLowerLevelValid()) {
                            val lowerLevel = surveyReportUiState.stairwayLowerLevel.toInt()
                            "Description preview for stairway: " +
                                    "'... between level $lowerLevel and ${lowerLevel+1}'..."
                        } else {
                            "Unable to show preview for stairway."
                        }
                    Text(text = stairwayHelpText)
                }

                LocationType.GROUND -> {
                    GroundOptionMenuSelection(surveyReportUiState.groundType) {
                        surveyReportViewModel.updateGroundType(it)
                    }
                }

                LocationType.MULTISTORYCARPARK -> {
                    TextInputTemplate(
                        fieldTitle = "Carpark Level",
                        fieldInput = surveyReportUiState.carparkLevel,
                        isFieldValid = surveyReportUiState.carparkLevelValid(),
                        onInputChange = { surveyReportViewModel.updateCarparkLevel(it) },
                        errorMessage = surveyReportUiState.carparkLevelError()
                    )
                }

                LocationType.ROOF -> {
                    //No roof specific information needed
                }
            }
            TextInputTemplate(
                fieldTitle = "Block number",
                fieldInput = surveyReportUiState.blockLocation,
                isFieldValid = surveyReportUiState.blockLocationValid(),
                onInputChange = { surveyReportViewModel.updateBlockLocation(it) },
                errorMessage = surveyReportUiState.blockLocationError()
            )
            TextInputTemplate(
                fieldTitle = "Street location",
                fieldInput = surveyReportUiState.streetLocation,
                isFieldValid = surveyReportUiState.streetLocationValid(),
                onInputChange = { surveyReportViewModel.updateStreetLocation(it) },
                errorMessage = surveyReportUiState.streetLocationError()
            )
            TextInputTemplate(
                fieldTitle = "Description of what is nearby (optional)",
                fieldInput = surveyReportUiState.nearbyDescription,
                isFieldValid = true,
                onInputChange = {surveyReportViewModel.updateNearbyDescription(it)},
                errorMessage = "Eg: 'near lift lobby A'" //Simple hint
            )
        } else {
            TextInputTemplate(
                fieldTitle = "Explanation of Infeasibility/Alternatives",
                fieldInput = surveyReportUiState.nonFeasibleExplanation,
                isFieldValid = surveyReportUiState.nonFeasibleExplanationValid(),
                onInputChange = {surveyReportViewModel.updateNonFeasibleExplanation(it)},
                errorMessage = surveyReportUiState.nonFeasibleExplanationError()
            )
        }

        Divider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text =
            "Add Additional Notes: "
                    + if (surveyReportUiState.hasAdditionalNotes) "Yes" else "No")
            Switch(
                checked = surveyReportUiState.hasAdditionalNotes,
                onCheckedChange = { surveyReportViewModel.updateHasAdditionalNotes(it) },
            )
        }
        if (surveyReportUiState.hasAdditionalNotes) {
            TextInputTemplate(
                fieldTitle = "Additional Notes",
                fieldInput = surveyReportUiState.techniciansNotes,
                isFieldValid = surveyReportUiState.techniciansNotesValid(),
                isFinalInput = true,
                onInputChange = { surveyReportViewModel.updateTechniciansNotes(it) },
                errorMessage = surveyReportUiState.techniciansNotesError(),
                isMultiLine = true
            )
            ImagePicker(
                "Accompanying Image",
                surveyReportUiState.extraImage,
                surveyReportUiState.editOnlyData?.extraImage?.bitmap){
                surveyReportViewModel.updateExtraImage(it)
            }
        }
        Divider()
        Button(onClick = {
            surveyReportViewModel.triggerPreview()
        },
            enabled = true
        ) {
            Text(text = "Preview")
        }
    }
}
@Preview
@Composable
fun LocationMenuSelectionPreview() {
    var selectedOption by remember { mutableStateOf(LocationType.CORRIDOR) }
    LocationMenuSelection(selectedOption) {
        selectedOption = it
    }
}

//Code taken from https://alexzh.com/jetpack-compose-dropdownmenu/
@Composable
fun LocationMenuSelection(
    selectedOption: LocationType,
    onOptionChange: (LocationType) -> Unit) {
    val options = LocationType.values().map{it.value}
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedOption.value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            expanded = false
                            val newOption = LocationType.values().find { it.value == item } ?: selectedOption
                            onOptionChange(newOption)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GroundOptionMenuSelection(
    selectedOption: GroundType,
    onOptionChange: (GroundType) -> Unit) {
    val options = GroundType.values().map{it.value}
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {
            TextField(
                value = selectedOption.value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            expanded = false
                            val newOption = GroundType.values().find { it.value == item } ?: selectedOption
                            onOptionChange(newOption)
                        }
                    )
                }
            }
        }
    }
}
@Preview
@Composable
fun ImagePickerPreview() {
    var imageFile by remember {
        mutableStateOf<File?>(null)
    }
    ImagePicker("Demo", imageFile, null) {
        imageFile = it
    }
}
@Composable
fun ImagePicker(
    imageCategory: String,
    image: File?,
    existingImage: Bitmap?,
    updateImage: (File?) -> Unit,
){
    val context = LocalContext.current
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {updateImage(if (it != null) getFile(context,it) else null)},
    )
    Column(
        modifier = Modifier
            .padding(mediumPadding),
        verticalArrangement = Arrangement.spacedBy(
            space = mediumPadding,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(imageCategory)
        image?.let {
            val source = ImageDecoder.createSource(it)
            Image(bitmap = ImageDecoder.decodeBitmap(source).asImageBitmap(),
                contentDescription =null,
                modifier = Modifier.size(400.dp))
            Text(image.name)
        } ?: existingImage?.let{btm ->
            Image(bitmap = btm.asImageBitmap(),
                contentDescription =null,
                modifier = Modifier.size(400.dp))
            Text("Image currently submitted")
        } ?: Text("You must have an image", color= MaterialTheme.colorScheme.error)
        Button(onClick = {
            launcher.launch("image/*")
        }) {
            Text(
                if (image == null){
                    if (existingImage == null) "Pick image"
                    else "Replace existing image"
                } else {
                    "Pick different Image"
                }
            )
        }
        if (image != null) {
            Button(onClick = { updateImage(null) }) {
                Text(if (existingImage == null) "Clear Image" else "Revert to existing image")
            }
        }
    }
}
@Preview
@Composable
fun DateTimePickerPreview(){
    var localDate by remember {
        mutableStateOf<LocalDate?>(null)
    }
    var localTime by remember {
        mutableStateOf<LocalTime?>(null)
    }
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(mediumPadding), //Padding
        verticalArrangement = Arrangement.spacedBy(
            space = mediumPadding,
            alignment = Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Survey DateTime")
        DateTimePicker(
            localDate,
            { localDate = it },
            localTime,
            { localTime = it }
        )
    }
}
@Composable
fun DateTimePicker(
    localDate: LocalDate?,
    updateDate: (LocalDate) -> Unit,
    localTime: LocalTime?,
    updateTime: (LocalTime) -> Unit,
){
    val dateDialogState = rememberMaterialDialogState()
    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        datepicker { updateDate(it) }
    }
    val timeDialogState = rememberMaterialDialogState()
    MaterialDialog(
        dialogState = timeDialogState,
        buttons = {
            positiveButton("Ok")
            negativeButton("Cancel")
        }
    ) {
        timepicker { updateTime(it) }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = localDate?.let{"Date: " + localDate.format(
                DateTimeFormatter.ofPattern("d/M/yyyy")
            )} ?: "Must have a Date",
            color = if (localDate == null) MaterialTheme.colorScheme.error
                    else Color.Unspecified
        )
        Button(onClick = { dateDialogState.show() }) {
            Text("Set Date")
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = localTime?.let {
                "Time: $localTime hrs"
            }?: "Must have a Time",
            color = if (localTime == null) MaterialTheme.colorScheme.error
            else Color.Unspecified
        )
        Button(onClick = { timeDialogState.show() }) {
            Text("Set Time")
        }
    }
}

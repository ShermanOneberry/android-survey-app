package com.oneberry.survey_report_app.ui.survey_report_activity

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oneberry.survey_report_app.R
import com.oneberry.survey_report_app.file_system.getFile
import java.io.File

@Composable
fun SurveyReportScreen(surveyReportViewModel: SurveyReportViewModel = viewModel()) {
    val surveyReportUiState by surveyReportViewModel.uiState.collectAsState()
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
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(mediumPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.app_name),
            style = typography.titleLarge,
        )
        TextInputTemplate(
            fieldTitle = "Batch number",
            fieldInput = surveyReportUiState.batchNum,
            isFieldValid = surveyReportUiState.batchNumValid(),
            isFinalInput = false,
            onInputChange = { surveyReportViewModel.updateBatchNum(it) },
            errorMessage = surveyReportUiState.batchNumError(),
        )
        TextInputTemplate(
            fieldTitle = "Survey number for batch",
            fieldInput = surveyReportUiState.intraBatchId,
            isFieldValid = surveyReportUiState.intraBatchIdValid(),
            isFinalInput = false,
            onInputChange = { surveyReportViewModel.updateIntraBatchId(it) },
            errorMessage = surveyReportUiState.intraBatchIdError(),
        )
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
            surveyReportUiState.reasonImage,){
            surveyReportViewModel.updateReasonImage(it)
        }
        if (isFeasible) {
            TextInputTemplate(
                fieldTitle = "Distance to Unit",
                fieldInput = surveyReportUiState.locationDistance,
                isFieldValid = surveyReportUiState.locationDistanceValid(),
                isFinalInput = false,
                onInputChange = { surveyReportViewModel.updateLocationDistance(it) },
                errorMessage = surveyReportUiState.locationDistanceError(),
            )
            TextInputTemplate(
                fieldTitle = "Camera Count",
                fieldInput = surveyReportUiState.cameraCount,
                isFieldValid = surveyReportUiState.cameraCountValid(),
                isFinalInput = false,
                onInputChange = { surveyReportViewModel.updateCameraCount(it) },
                errorMessage = surveyReportUiState.cameraCountError()
            )
            TextInputTemplate(
                fieldTitle = "Box Count",
                fieldInput = surveyReportUiState.boxCount,
                isFieldValid = surveyReportUiState.boxCountValid(),
                isFinalInput = false,
                onInputChange = { surveyReportViewModel.updateBoxCount(it) },
                errorMessage = surveyReportUiState.boxCountError()
            )

            LocationMenuSelection(surveyReportUiState.locationType) {
                surveyReportViewModel.updateLocationType(it)
            }
            when (surveyReportUiState.locationType) {
                LocationType.CORRIDOR -> {
                    TextInputTemplate(
                        fieldTitle = "Corridor Level",
                        fieldInput = surveyReportUiState.corridorLevel,
                        isFieldValid = surveyReportUiState.corridorLevelValid(),
                        isFinalInput = false,
                        onInputChange = { surveyReportViewModel.updateCorridorLevel(it) },
                        errorMessage = surveyReportUiState.corridorLevelError()
                    )
                }

                LocationType.STAIRWAY -> {
                    TextInputTemplate(
                        fieldTitle = "Lower Level",
                        fieldInput = surveyReportUiState.stairwayLowerLevel,
                        isFieldValid = surveyReportUiState.stairwayLowerLevelValid(),
                        isFinalInput = false,
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
                    //TODO: Discuss whether anything specific would be needed for this category
                }

                LocationType.MULTISTORYCARPARK -> {
                    TextInputTemplate(
                        fieldTitle = "Carpark Level",
                        fieldInput = surveyReportUiState.carparkLevel,
                        isFieldValid = surveyReportUiState.carparkLevelValid(),
                        isFinalInput = false,
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
                isFinalInput = false,
                onInputChange = { surveyReportViewModel.updateBlockLocation(it) },
                errorMessage = surveyReportUiState.blockLocationError()
            )
            TextInputTemplate(
                fieldTitle = "Street location",
                fieldInput = surveyReportUiState.streetLocation,
                isFieldValid = surveyReportUiState.streetLocationValid(),
                isFinalInput = false,
                onInputChange = { surveyReportViewModel.updateStreetLocation(it) },
                errorMessage = surveyReportUiState.streetLocationError()
            )
            TextInputTemplate(
                fieldTitle = "Description of what is nearby (optional)",
                fieldInput = surveyReportUiState.nearbyDescription,
                isFieldValid = true,
                isFinalInput = false,
                onInputChange = {surveyReportViewModel.updateNearbyDescription(it)},
                errorMessage = null //TODO: Maybe add suggested input here?
            )
            //TODO: Add nearby clarification info and context sensitive hint system
            //      Eg: 'Corridor' is "near Lift Lobby 3A", while 'Ground' is "on grass patch"
            //      Also add preview system for how location description will be generated on the server
        } else {
            TextInputTemplate(
                fieldTitle = "Explanation of Infeasibility/Alternatives",
                fieldInput = surveyReportUiState.nonFeasibleExplanation,
                isFieldValid = surveyReportUiState.nonFeasibleExplanationValid(),
                isFinalInput = false,
                onInputChange = {surveyReportViewModel.updateNonFeasibleExplanation(it)},
                errorMessage = surveyReportUiState.nonFeasibleExplanationError()
            )
        }
        TextInputTemplate(
            fieldTitle = "Additional Notes",
            fieldInput = surveyReportUiState.techniciansNotes,
            isFieldValid = true,
            isFinalInput = true,
            onInputChange = {surveyReportViewModel.updateTechniciansNotes(it)},
            errorMessage = null
        )
        Button(onClick = {
            Log.d("ButtonEvent", "This should not happen quickly");
            surveyReportViewModel.triggerSubmission()
        },
            enabled = true
        ) {
            Text(text = "Submit")
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
    val context = LocalContext.current
    val options = LocationType.values().map{it.value}
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            //.padding(32.dp) //TODO: See if this is actually needed in the full UI
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
    isFinalInput: Boolean
) {
    //TODO: Should this focusManger be called here or from the main screen composable?
    //TODO: Should I even bother with focus management? Take out if this causes too many bugs
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        value = fieldInput,
        singleLine = true,
        shape = shapes.large,
        modifier = Modifier.fillMaxWidth(),
        //colors = TextFieldDefaults.textFieldColors(containerColor = colorScheme.surface),
        onValueChange = onInputChange,
        label = {Text(fieldTitle)},
        isError = !isFieldValid,
        supportingText = if (errorMessage == null) null else { {Text(errorMessage)} },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = if (isFinalInput) ImeAction.Done else ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onDone = { },
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
        )
    )
}
@Preview
@Composable
fun ImagePickerPreview() {
    var imageFile by remember {
        mutableStateOf<File?>(null)
    }
    ImagePicker("Demo", imageFile) {
        imageFile = it
    }
}
@Composable
fun ImagePicker(imageCategory: String, image: File?, updateImage: (File?) -> Unit){
    val context = LocalContext.current
    val bitmap =  remember {
        mutableStateOf<Bitmap?>(null)
    }
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {updateImage(if (it != null) getFile(context,it) else null)}, //TODO: Check what happens if we don't declare an image
    )
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
        Button(onClick = {
            launcher.launch("image/*")
        }) {
            Text(text = if (image == null) "Pick image"
            else "Pick different Image")
        }
    }
}
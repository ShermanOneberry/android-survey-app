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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.oneberry.survey_report_app.R
import com.oneberry.survey_report_app.data.GroundType
import com.oneberry.survey_report_app.data.LocationType
import com.oneberry.survey_report_app.util.getFile
import java.io.File

@Composable
fun SurveyReportScreen(
    surveyReportViewModel: SurveyReportViewModel =
        viewModel(factory = SurveyReportViewModel.Factory),
    navigateToLogin: (String) -> Unit,
    navigateToPreview: () -> Unit,
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
    LaunchedEffect(Unit) {
        if (appHasLoaded) {
            return@LaunchedEffect
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
        } else {
            Button(onClick = { navigateToLogin("") }) {
                Text("Login")
            }
        }

        Divider()

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
            surveyReportUiState.reasonImage,){
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
                surveyReportUiState.extraImage,){
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
        shape = shapes.large,
        modifier =
            if (isMultiLine)
                Modifier
                    .fillMaxWidth()
                    .heightIn(1.dp, Dp.Infinity)
            else Modifier.fillMaxWidth(),
        //colors = TextFieldDefaults.textFieldColors(containerColor = colorScheme.surface),
        onValueChange = onInputChange,
        label = {Text(fieldTitle)},
        isError = !isFieldValid,
        supportingText = if (errorMessage == null) null else { {Text(errorMessage)} },
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
            val source = ImageDecoder
                .createSource(it)
            bitmap.value = ImageDecoder.decodeBitmap(source)

            bitmap.value?.let {  btm ->
                Image(bitmap = btm.asImageBitmap(),
                    contentDescription =null,
                    modifier = Modifier.size(400.dp))
            }
            Text(image.name)
        } ?: Text("You must have an image", color= MaterialTheme.colorScheme.error)
        Button(onClick = {
            launcher.launch("image/*")
        }) {
            Text(text = if (image == null) "Pick image"
            else "Pick different Image")
        }
    }
}

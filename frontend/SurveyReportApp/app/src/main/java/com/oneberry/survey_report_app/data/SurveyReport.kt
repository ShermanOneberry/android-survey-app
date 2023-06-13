package com.oneberry.survey_report_app.data

import android.graphics.Bitmap
import java.io.File
import java.time.LocalDate
import java.time.LocalTime


data class SurveyReport(
    @Transient
    val isNewReport: Boolean = true,
    @Transient
    val editOnlyImages: EditOnlyImages? = null,
    @Transient
    val batchNum: String = "1",
    @Transient
    val intraBatchId: String = "1",
    //Metadata about report
    val surveyDate: LocalDate? = null,
    val surveyTime: LocalTime? = null,

    val isFeasible: Boolean = true,
    @Transient //Provide image separately, rather than serialize
    val reasonImage: File? = null,

    //Not feasible description
    val nonFeasibleExplanation: String = "",

    //Basic feasible info
    val locationDistance: String = "1m", //Unit: Metres
    val cameraCount: String = "1",
    val boxCount: String = "1",

    val locationType: LocationType = LocationType.CORRIDOR,
    //NOTE: I use product type to allow for form memory when filling in the form
    //Template for Corridor
    val corridorLevel: String = "2",
    //Template for Stairway
    val stairwayLowerLevel: String = "1",
    //Template for Ground
    val groundType: GroundType = GroundType.OTHER, //TODO: Integrate this option
    //Template for MSCP
    val carparkLevel: String = "3A",
    //No template for roof

    //Extra info (for all location types) (General location)
    val blockLocation: String = "Blk 1A",
    val streetLocation: String = "Sims Drive",
    val nearbyDescription: String = "", //Optional and context sensitive

    val hasAdditionalNotes: Boolean = false, //TODO: Integrate this option
    val techniciansNotes: String = "",
    @Transient //Provide image separately, rather than serialize
    val extraImage: File? = null, //NOTE: NOT ALWAYS MANDATORY, ONLY WHEN WE HAVE ADDON NOTES

) {
    private fun isNumberFieldValid(field: String, minimumValue: Int = 1): Boolean {
        return field.toIntOrNull() != null && field.toInt() >= minimumValue
    }
    fun batchNumValid(): Boolean {
        return isNumberFieldValid(batchNum)
    }
    fun batchNumError(): String? {
        return if (batchNumValid()) return null
        else "Batch number is required"
    }
    fun intraBatchIdValid(): Boolean {
        return isNumberFieldValid(intraBatchId)
    }
    fun intraBatchIdError(): String? {
        return if (batchNumValid()) return null
        else "Survey number for batch is required"
    }
    fun locationDistanceValid(): Boolean {
        val distanceUnitless = locationDistance.substring(0, locationDistance.length - 1)
        return isNumberFieldValid(distanceUnitless) && locationDistance.last() == 'm'
    }
    fun locationDistanceError(): String? {
        return if (locationDistanceValid()) null
        else "Input must follow the format '123m'"
    }
    fun nonFeasibleExplanationValid(): Boolean {return nonFeasibleExplanation.trim().isNotEmpty()}
    fun nonFeasibleExplanationError(): String? {
        return if (nonFeasibleExplanationValid()) null
        else "Must include Explanation"
    }
    fun cameraCountValid(): Boolean {return isNumberFieldValid(cameraCount)}
    fun cameraCountError(): String? {
        return if (cameraCountValid()) null
        else "Camera count must be a positive number"
    }
    fun boxCountValid(): Boolean {return isNumberFieldValid(boxCount)}
    fun boxCountError(): String? {
        return if (boxCountValid()) null
        else "Box count must be a positive number"
    }
    fun corridorLevelValid(): Boolean {return isNumberFieldValid(corridorLevel, 2)}
    fun corridorLevelError(): String? {
        return if (corridorLevelValid()) null
        else if (isNumberFieldValid(corridorLevel,1)) "Must use Ground Location Type instead"
        else "Corridor Level must be a number 2 or greater"
    }
    fun stairwayLowerLevelValid(): Boolean {return isNumberFieldValid(stairwayLowerLevel)}
    fun stairwayLowerLevelError(): String? {
        return if (stairwayLowerLevelValid()) null
        else "Stairway Lower Level must be a single, positive number"
    }

    fun carparkLevelValid(): Boolean{
        return "^[1-9]\\d?[AB]?\$".toRegex().matches(carparkLevel) //TODO, Test this
    }
    fun carparkLevelError(): String? {
        return if (carparkLevelValid()) null
        else if (carparkLevel.trim().first() == '0') "Level cannot start with zero"
        else if ("^[1-9]\\d*[AB]?\$".toRegex().matches(carparkLevel)) "Carpark Level is too high"
        else "Level must either end with A, B, or be bare"
    }
    fun blockLocationValid(): Boolean{
        return "^Blk +[1-9]\\d{0,3}[A-Z]?$".toRegex().matches(blockLocation)
    }
    fun blockLocationError(): String? {
        return if (blockLocationValid()) null
        else if ("^Blk +[1-9]\\d{0,3}[a-z]$".toRegex().matches(blockLocation))
            "Block number cannot end with lowercase letter"
        else if ("^Blk +[1-9]\\d{0,3}[A-Z].$".toRegex().matches(blockLocation))
            "Block number must end with at most one letter"
        else if ("^Blk 0".toRegex().matches(blockLocation))
            "Block number cannot start with a zero"
        else if ("^Blk +[1-9]\\d{4,}[A-Z]?$".toRegex().matches(blockLocation))
            "Block number too large"
        else "The Blk number must follow the format '51' or '51A'"
    }
    fun streetLocationValid(): Boolean{
        return streetLocation.trim().isNotEmpty()
    }
    fun streetLocationError(): String? {
        return if (streetLocationValid()) null
        else "Street Location cannot be left blank"
    }
    fun techniciansNotesValid(): Boolean {
        return techniciansNotes.trim().isNotEmpty()
    }
    fun techniciansNotesError(): String? {
        return if (techniciansNotesValid()) null
        else "Cannot have empty notes"
    }
    fun overallSurveyValid(): Boolean { //Note: Update this along side actual fields
        if (
            !batchNumValid() ||
            !intraBatchIdValid() ||
            surveyDate == null ||
            surveyTime == null||
            reasonImage == null
        ) return false

        if (hasAdditionalNotes) {
            if (extraImage == null || !techniciansNotesValid()) {
                return false
            }
        }

        if (isFeasible) {
            when(locationType) {
                LocationType.CORRIDOR -> {
                    if (!corridorLevelValid()) return false
                }
                LocationType.STAIRWAY -> {
                    if (!stairwayLowerLevelValid()) return false
                }
                LocationType.GROUND -> {
                    //No extra requirements
                }
                LocationType.MULTISTORYCARPARK -> {
                    if (!carparkLevelValid()) return false
                }
                LocationType.ROOF -> {
                    //No extra requirements
                }
            }
            return cameraCountValid() && boxCountValid() && locationDistanceValid() &&
                    blockLocationValid() && streetLocationValid()
        } else {
            return nonFeasibleExplanationValid()
        }
    }
}

enum class LocationType(val value: String){
    CORRIDOR("Corridor"),
    STAIRWAY("Stairway"),
    GROUND("Ground"),
    MULTISTORYCARPARK("Carpark"),
    ROOF("Roof")
}
enum class GroundType(val value: String){
    VOID_DECK("Void Deck"),
    GRASS_PATCH("Grass patch"),
    OTHER("<Other>")
}

data class EditOnlyImages(
    val reasonImage: Bitmap,
    val extraImage: Bitmap?,
)
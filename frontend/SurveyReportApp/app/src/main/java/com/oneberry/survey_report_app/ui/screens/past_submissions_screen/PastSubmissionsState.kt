package com.oneberry.survey_report_app.ui.screens.past_submissions_screen

import com.oneberry.survey_report_app.network.api_body.ItemsData

data class PastSubmissionsState (
    val searchBox: PastSubmissionsSearchBoxState = PastSubmissionsSearchBoxState(),
    val apiState: PastSubmissionsApiState? = null,
)

data class PastSubmissionsSearchBoxState (
    //Display
    val block: String = "",
    val street: String = "",
    //Used strings for actual api calls
    val blockFilter: String = "",
    val streetFilter: String = "",
)

data class PastSubmissionsApiState (
    val page: Int,
    val perPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val items: List<AugmentedItemData>,

    val latestBatchNumber: Int,
)
data class AugmentedItemData (
    val item: ItemsData,
    val sameUser: Boolean,
)
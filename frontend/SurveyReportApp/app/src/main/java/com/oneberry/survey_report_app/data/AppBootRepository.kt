package com.oneberry.survey_report_app.data

import kotlinx.coroutines.flow.MutableStateFlow

class AppBootRepository {
    val appHasLoaded = MutableStateFlow(false)
}

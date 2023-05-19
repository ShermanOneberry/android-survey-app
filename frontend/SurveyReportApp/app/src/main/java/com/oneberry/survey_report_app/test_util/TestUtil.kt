package com.oneberry.survey_report_app.test_util

import android.os.Build


fun isUnitTest(): Boolean {
    var device = Build.DEVICE
    var product = Build.PRODUCT
    if (device == null) {
        device = ""
    }
    if (product == null) {
        product = ""
    }
    return (device == "robolectric") && (product == "robolectric")
}
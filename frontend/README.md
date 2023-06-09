# Survey Report App

SurveyReportApp is an Android app to fill in a survey report for Oneberry

## Installation

Generate the APK in Android Studio by clicking on `Build > Build Bundle(s) / APK(s) > Build APK(s)`

**Note** that the current setup will generate debug APKs only. See Android Studio documentation to set up production APK builds

### How to update API Endpoint

If you need to change the API Endpoint (that is, where the Pocketbase backend is being hosted), you will need to update  `frontend\SurveyReportApp\app\src\main\java\com\oneberry\survey_report_app\SurveyApplication.kt`.

There, you will need to update the line `const val API_URL = "http://10.0.2.2:8090"`. If the new backend is at `https://example.com` (and it should be https), the line should be `const val API_URL = "https://example.com"`.
- Note that there must **NOT** be any trailing foward slashes. So `https://example.com/` is incorrect, and must instead be `https://example.com`.

Further note that in production, you will need to remove from `frontend\SurveyReportApp\app\src\main\AndroidManifest.xml`, the line `android:usesCleartextTraffic="true"`, since this permission is only needed when using test URLs with unencrypted http. 

Afterwards, the APK will need to be recompiled and redistributed.

## Usage

See `Usage.pdf` for how to use the app.

Note that `Usage.pdf` is built from `Usage.marp` under the Marp framework. See https://marp.app/ for details 

## Contributing

//TO BE DECIDED

## License

//TO BE DECIDED
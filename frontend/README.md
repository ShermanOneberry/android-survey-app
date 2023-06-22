# Survey Report App

SurveyReportApp is an Android app to fill in a survey report for Oneberry

## Installation

//TODO: Add installation here once I've got an APK

### How to update API Endpoint

If you need to change the API Endpoint (that is, where the Pocketbase backend is being hosted), you will need to update  `frontend\SurveyReportApp\app\src\main\java\com\oneberry\survey_report_app\SurveyApplication.kt`.

There, you will need to update the line `const val API_URL = "http://10.0.2.2:8090"`. If the new backend is at `https://example.com` (and it should be https), the line should be `const val API_URL = "https://example.com"`.
- Note that there must **NOT** be any trailing foward slashes. So `https://example.com/` is incorrect, and must instead be `https://example.com`.

Afterwards, the APK will need to be recompiled and redistributed.

## Usage

//TODO: Add a guide here with pictures

## Contributing

//TO BE DECIDED

## License

//TO BE DECIDED
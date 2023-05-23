package com.oneberry.survey_report_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.oneberry.survey_report_app.ui.navigation.NavGraph
import com.oneberry.survey_report_app.ui.theme.SurveyReportAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SurveyReportAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(title = {
                                Text(stringResource(R.string.app_name))
                            })
                        }
                    ) { contentPadding ->
                        MainScreen(contentPadding)
                    }
                }
            }
        }
    }
}

@Composable
private fun MainScreen(contentPadding: PaddingValues) {
    val navController = rememberNavController()
    NavGraph(navController, contentPadding)
}
package com.oneberry.survey_report_app.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.oneberry.survey_report_app.ui.screens.login_screen.LoginScreen
import com.oneberry.survey_report_app.ui.screens.preview_screen.PreviewScreen
import com.oneberry.survey_report_app.ui.screens.survey_report_screen.SurveyReportScreen

@Composable
fun NavGraph(navController: NavHostController, contentPadding: PaddingValues) {

    NavHost(
        navController = navController,
        startDestination = NavRoute.Form.path
    ) {
        addFormScreen(navController, this, contentPadding)
        addLoginScreen(navController, this, contentPadding)
        addPreviewScreen(navController, this, contentPadding)
    }
}

private fun addFormScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    contentPadding: PaddingValues
) {
    navGraphBuilder.composable(route = NavRoute.Form.path) {
        SurveyReportScreen(
            navigateToLogin = { initialUsername:String ->
                navController.navigate(
                    NavRoute.Login.withArgs(initialUsername)
                )
            },
            navigateToPreview = {
                navController.navigate(NavRoute.Preview.path)
            },
            contentPadding = contentPadding
        )
    }
}

private fun addLoginScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    contentPadding: PaddingValues
) {
    navGraphBuilder.composable(
        route = NavRoute.Login.withArgsFormat(
            NavRoute.Login.username
        ),
        arguments = listOf(
            navArgument(NavRoute.Login.username) {
                type = NavType.StringType
                nullable = false
            }
        )
    ) {
        LoginScreen(
            popBackStack = { navController.popBackStack() },
            contentPadding = contentPadding,
        )
    }
}


fun addPreviewScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    contentPadding: PaddingValues,
) {
    navGraphBuilder.composable(route = NavRoute.Preview.path) {
        PreviewScreen(
            navigateToLogin = { initialUsername:String ->
                navController.navigate(
                    NavRoute.Login.withArgs(initialUsername)
                )
            },
            popBackStack = { navController.popBackStack() },
            contentPadding = contentPadding,
        )
    }
}

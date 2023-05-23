package com.oneberry.survey_report_app.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.oneberry.survey_report_app.ui.screens.login_screen.LoginScreen
import com.oneberry.survey_report_app.ui.screens.survey_report_screen.SurveyReportScreen

@Composable
fun NavGraph(navController: NavHostController, contentPadding: PaddingValues) {

    NavHost(
        navController = navController,
        startDestination = NavRoute.Form.path
    ) {
        addFormScreen(navController, this, contentPadding)
        addLoginScreen(navController, this, contentPadding)
        //addPreviewScreen(navController, this)
    }
}
private fun addFormScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    contentPadding: PaddingValues
) {
    navGraphBuilder.composable(route = NavRoute.Form.path) {
        SurveyReportScreen(
            navigateToLogin = {
                navController.navigate(NavRoute.Login.path)
            },
            navigateToPreview = {
                TODO()
            },
            contentPadding = contentPadding
        )
    }
}
private fun popUpToForm(navController: NavHostController) {
    navController.popBackStack(NavRoute.Form.path, inclusive = false)
}

private fun addLoginScreen(
    navController: NavHostController,
    navGraphBuilder: NavGraphBuilder,
    contentPadding: PaddingValues
) {
    navGraphBuilder.composable(route = NavRoute.Login.path) {
        LoginScreen(
            popBackStack = { navController.popBackStack() },
            popUpToForm = { popUpToForm(navController) },
            contentPadding = contentPadding,
        )
    }
}

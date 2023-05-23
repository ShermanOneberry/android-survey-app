package com.oneberry.survey_report_app.ui.navigation

sealed class NavRoute(val path: String) {

    object Login: NavRoute("login") {
    }

    object Form: NavRoute("home")

    object Preview: NavRoute("preview")

    // build navigation path (for screen navigation)
    fun withArgs(vararg args: String): String {
        return buildString {
            append(path)
            args.forEach{ arg ->
                append("/$arg")
            }
        }
    }

    // build and setup route format (in navigation graph)
    fun withArgsFormat(vararg args: String) : String {
        return buildString {
            append(path)
            args.forEach{ arg ->
                append("/{$arg}")
            }
        }
    }
}

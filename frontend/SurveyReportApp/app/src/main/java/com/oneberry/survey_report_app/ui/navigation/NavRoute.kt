package com.oneberry.survey_report_app.ui.navigation

sealed class NavRoute(val path: String) {
    //Based on: https://vtsen.hashnode.dev/simple-jetpack-compose-navigation-example

    object Login: NavRoute("login") {
        val username = "username"
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

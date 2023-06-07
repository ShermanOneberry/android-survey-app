package com.oneberry.survey_report_app.ui.navigation

sealed class NavRoute(val path: String) {
    //Based on: https://vtsen.hashnode.dev/simple-jetpack-compose-navigation-example
    private var argNames: Array<out String> = arrayOf()

    object Login: NavRoute("login") {
        const val username = "username"
    }

    object Form: NavRoute("home")

    object Preview: NavRoute("preview")

    // build navigation path (for screen navigation)
    fun forArgs(vararg args: String):NavRoute {
        argNames = args
        return this
    }
    fun withArgs(vararg args: String): String {
        return buildString {
            append(path)
            argNames.zip(args).forEach{ (argName, argValue) ->
                append("/?$argName=$argValue")
            }
        }
    }

    // build and setup route format (in navigation graph)
    fun withArgsFormat(vararg args: String) : String {
        return buildString {
            append(path)
            args.forEach{ arg ->
                append("/?$arg={$arg}")
            }
        }
    }
}

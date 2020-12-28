package io.github.diubruteforce.smartcr.navigation

import androidx.navigation.NavBackStackEntry

/*
* This is how I am going to create route
* For every combination of arg I will create
* a new class and create an object of that
* class to create a route
*
* route variable to get the route
*
* uir to navigate to the route that will
* take all the args as parameter
*
* for every argument a dedicated method
* to get that argument
*
* In future I may write kotlin compiler plugin to do this
* */
open class NoArgRoute(private val path: String) {
    val route = path
    fun uri() = path
}

class SingleArgRoute(private val path: String, private val argName: String) {
    val route = "$path/{$argName}"
    fun uri(arg: String) = "$path/$arg"
    fun getArgument(backStackEntry: NavBackStackEntry): String =
        backStackEntry.arguments?.getString(argName)!!
}

class DoubleArgRoute(
    private val path: String,
    private val firstArgName: String,
    private val secondArgName: String
) {
    val route = "$path/{$firstArgName}/{$secondArgName}"

    fun uri(firstArg: String, secondArg: String) = "$path/$firstArg/$secondArg"

    fun getArgument(backStackEntry: NavBackStackEntry): Pair<String, String> {
        val firstArg = backStackEntry.arguments?.getString(firstArgName)!!
        val secondArg = backStackEntry.arguments?.getString(secondArgName)!!

        return Pair(firstArg, secondArg)
    }
}

class SingleOptionalArgRoute(private val path: String, private val argName: String) {
    val route = "$path?$argName={$argName}"
    fun uri(arg: String? = null) = if (arg != null) "$path?$argName=$arg" else path
    fun getArgument(backStackEntry: NavBackStackEntry): String? =
        backStackEntry.arguments?.getString(argName)
}

class RequiredAndOptionalArgRoute(
    private val path: String,
    private val requiredArgName: String,
    private val optionArgName: String
) {
    val route = "$path/{$requiredArgName}?$optionArgName={$optionArgName}"

    fun uri(requiredArg: String, optionArg: String? = null): String {
        return if (optionArg != null) "$path/$requiredArg?$optionArgName=$optionArg"
        else "$path/$requiredArg"
    }

    fun getArgument(backStackEntry: NavBackStackEntry): Pair<String, String?> {
        val required = backStackEntry.arguments?.getString(requiredArgName)!!
        val optional = backStackEntry.arguments?.getString(optionArgName)

        return Pair(required, optional)
    }
}
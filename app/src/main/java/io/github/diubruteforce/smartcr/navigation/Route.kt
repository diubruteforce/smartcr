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
class NoArgRoute(private val path: String) {
    val route = path
    fun uri() = path
}

class SingleArgRoute(private val path: String, private val argName: String) {
    val route = "$path/$argName"
    fun uri(arg: String) = "$path/$arg"
    fun getArgument(backStackEntry: NavBackStackEntry): String =
        backStackEntry.arguments?.getString(argName)!!
}
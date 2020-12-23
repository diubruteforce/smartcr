package io.github.diubruteforce.smartcr.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import io.github.diubruteforce.smartcr.di.hiltViewModel
import io.github.diubruteforce.smartcr.ui.home.HomeScreen
import io.github.diubruteforce.smartcr.ui.profile.student.StudentEditScreen

@Composable
fun SmartCRApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainRoute.Auth.route
    ) {
        authNavigation(
            navController = navController,
            route = MainRoute.Auth.route
        )

        composable(MainRoute.SmartCR.route) {
            HomeScreen(navigateToOnBoarding = { navController.navigate(MainRoute.Auth.uri()) })
        }

        composable(MainRoute.StudentProfileEdit.route) {
            StudentEditScreen(
                viewModel = hiltViewModel(),
                onBackPress = null,
                onNavigateToHome = {
                    navController.navigate(MainRoute.SmartCR.uri()) { launchSingleTop = true }
                }
            )
        }
    }
}

object MainRoute {
    val Auth = NoArgRoute("Auth")
    val SmartCR = NoArgRoute("SmartCR")
    val StudentProfileEdit = NoArgRoute("StudentProfileEdit")
}
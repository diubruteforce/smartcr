package io.github.diubruteforce.smartcr.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import io.github.diubruteforce.smartcr.di.hiltViewModel
import io.github.diubruteforce.smartcr.ui.profile.student.StudentEditScreen
import io.github.diubruteforce.smartcr.ui.smartcr.SmartCRScreen
import io.github.diubruteforce.smartcr.ui.smartcr.menu.Menu

@Composable
fun SmartCRApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainRoute.SmartCR.route
    ) {
        authNavigation(
            navController = navController,
            route = MainRoute.Auth.route
        )

        composable(MainRoute.SmartCR.route) {
            SmartCRScreen(
                onMenuClick = { menu ->
                    when (menu) {
                        Menu.FIND_FACULTY -> {
                        }
                        Menu.FIND_COURSE -> {
                        }
                        Menu.GET_RESOURCE -> {
                        }
                        Menu.EXTRA_CLASS -> {
                        }
                        Menu.EXAM_ROUTINE -> {
                        }
                        Menu.FEES_SCHEDULE -> {
                        }
                        Menu.YOUR_PROFILE -> {
                        }
                        Menu.APP_SETTING -> {
                        }
                    }
                }
            )
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
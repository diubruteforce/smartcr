package io.github.diubruteforce.smartcr.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import io.github.diubruteforce.smartcr.di.hiltViewModel
import io.github.diubruteforce.smartcr.ui.smartcr.SmartCRScreen
import io.github.diubruteforce.smartcr.ui.smartcr.menu.Menu
import io.github.diubruteforce.smartcr.ui.student.StudentEditScreen
import io.github.diubruteforce.smartcr.ui.teacher.TeacherEditScreen

@Composable
fun SmartCRApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainRoute.TeacherProfileEdit.route
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

        composable(MainRoute.TeacherProfileEdit.route) {
            val teacherId = MainRoute.TeacherProfileEdit.getArgument(it)

            TeacherEditScreen(
                viewModel = hiltViewModel(),
                teacherId = teacherId,
                navigateToTeacherList = { /*TODO*/ },
                onBackPress = navController::navigateUp
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
    val TeacherProfileEdit = SingleOptionalArgRoute("StudentProfileEdit", "teacherId")
}
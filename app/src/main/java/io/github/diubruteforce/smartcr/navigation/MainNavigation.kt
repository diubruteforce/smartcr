package io.github.diubruteforce.smartcr.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import io.github.diubruteforce.smartcr.di.hiltViewModel
import io.github.diubruteforce.smartcr.ui.course.CourseListScreen
import io.github.diubruteforce.smartcr.ui.section.SectionEditScreen
import io.github.diubruteforce.smartcr.ui.section.SectionListScreen
import io.github.diubruteforce.smartcr.ui.smartcr.SmartCRScreen
import io.github.diubruteforce.smartcr.ui.smartcr.menu.Menu
import io.github.diubruteforce.smartcr.ui.student.StudentDetailScreen
import io.github.diubruteforce.smartcr.ui.student.StudentEditScreen
import io.github.diubruteforce.smartcr.ui.teacher.TeacherDetailScreen
import io.github.diubruteforce.smartcr.ui.teacher.TeacherEditScreen
import io.github.diubruteforce.smartcr.ui.teacher.TeacherListScreen

@Composable
fun SmartCRApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainRoute.SectionList.route
    ) {
        authNavigation(
            navController = navController,
            route = MainRoute.Auth.route
        )

        // region: SmartCR
        composable(MainRoute.SmartCR.route) {
            SmartCRScreen(
                onMenuClick = { menu ->
                    when (menu) {
                        Menu.FIND_FACULTY -> {
                            navController.navigate(MainRoute.TeacherList.uri())
                        }
                        Menu.FIND_COURSE -> {
                            navController.navigate(MainRoute.CourseList.uri())
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
                            navController.navigate(MainRoute.StudentProfileDetail.uri())
                        }
                        Menu.APP_SETTING -> {
                        }
                    }
                }
            )
        }
        // endregion

        // region: TeacherProfileEdit
        composable(MainRoute.TeacherProfileEdit.route) {
            val teacherId = MainRoute.TeacherProfileEdit.getArgument(it)

            TeacherEditScreen(
                viewModel = hiltViewModel(),
                teacherId = teacherId,
                onBackPress = navController::navigateUp
            )
        }
        // endregion

        // region: TeacherDetail
        composable(MainRoute.TeacherDetail.route) {
            val teacherId = MainRoute.TeacherDetail.getArgument(it)

            TeacherDetailScreen(
                viewModel = hiltViewModel(),
                teacherId = teacherId,
                navigateToTeacherEdit = {
                    navController.navigate(MainRoute.TeacherProfileEdit.uri(it))
                },
                onBackPress = navController::navigateUp
            )
        }
        // endregion

        // region: TeacherList
        composable(MainRoute.TeacherList.route) {
            TeacherListScreen(
                viewModel = hiltViewModel(),
                onBackPress = navController::navigateUp,
                navigateToTeacherDetail = {
                    navController.navigate(MainRoute.TeacherDetail.uri(it))
                },
                addNewTeacher = {
                    navController.navigate(MainRoute.TeacherProfileEdit.uri())
                }
            )
        }
        // endregion

        // region: StudentProfileEdit
        composable(MainRoute.StudentProfileEdit.route) {
            val isFirstTime = MainRoute.StudentProfileEdit.getArgument(it) == null
            val onBackPress: (() -> Unit)? = if (isFirstTime) null else navController::navigateUp

            StudentEditScreen(
                viewModel = hiltViewModel(),
                onBackPress = onBackPress,
                onNavigateToHome = {
                    navController.navigate(MainRoute.SmartCR.uri()) { launchSingleTop = true }
                }
            )
        }
        // endregion

        // region: StudentProfileDetail
        composable(MainRoute.StudentProfileDetail.route) {
            StudentDetailScreen(
                viewModel = hiltViewModel(),
                navigateToOnBoarding = {
                    navController.navigate(MainRoute.Auth.route)
                },
                navigateToProfileEdit = {
                    navController.navigate(MainRoute.StudentProfileEdit.uri("backPress"))
                },
                onBackPress = navController::navigateUp
            )
        }
        // endregion

        // region: CourseList
        composable(MainRoute.CourseList.route) {
            CourseListScreen(
                viewModel = hiltViewModel(),
                navigateToSectionList = { },
                onBackPress = navController::navigateUp
            )
        }
        // endregion

        //region: SectionEdit
        composable(MainRoute.SectionEdit.route) {
            SectionEditScreen(
                viewModel = hiltViewModel(),
                sectionId = "TS2CxBowARiLEsEPUYEP",
                courseId = "0C7BCeQAEBaGfNbS0NrT",
                navigateToTeacherEdit = {
                    navController.navigate(MainRoute.TeacherProfileEdit.uri())
                },
                onBackPress = navController::navigateUp
            )
        }
        // endregion

        // region: SectionList
        composable(MainRoute.SectionList.route) {
            SectionListScreen(
                viewModel = hiltViewModel(),
                courseId = "0C7BCeQAEBaGfNbS0NrT",
                navigateToSectionDetail = { /*TODO*/ },
                createNewSection = { navController.navigate(MainRoute.SectionEdit.uri(it)) },
                onBackPress = navController::navigateUp
            )
        }
        // endregion

    }
}

object MainRoute {
    val Auth = NoArgRoute("Auth")
    val SmartCR = NoArgRoute("SmartCR")

    val StudentProfileEdit = SingleOptionalArgRoute("StudentProfileEdit", "first")
    val StudentProfileDetail = NoArgRoute("StudentProfileDetail")

    val TeacherProfileEdit = SingleOptionalArgRoute("TeacherProfileEdit", "teacherId")
    val TeacherDetail = SingleArgRoute("TeacherDetail", "teacherId")
    val TeacherList = NoArgRoute("TeacherList")

    val CourseList = NoArgRoute("CourseList")

    val SectionEdit = RequiredAndOptionalArgRoute(
        path = "SectionEdit",
        requiredArgName = "courseId",
        optionArgName = "sectionId"
    )
    val SectionList = SingleArgRoute("SectionList", "courseId")
}
package io.github.diubruteforce.smartcr.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import io.github.diubruteforce.smartcr.di.hiltViewModel
import io.github.diubruteforce.smartcr.model.data.PostType
import io.github.diubruteforce.smartcr.ui.course.CourseListScreen
import io.github.diubruteforce.smartcr.ui.post.PostEditScreen
import io.github.diubruteforce.smartcr.ui.section.SectionDetailScreen
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
        startDestination = MainRoute.Auth.route
    ) {
        authNavigation(
            navController = navController,
            route = MainRoute.Auth.route
        )

        // region: SmartCR
        composable(MainRoute.SmartCR.route) {
            SmartCRScreen(
                navigateToSectionDetail = {
                    navController.navigate(MainRoute.SectionDetail.uri(it))
                },
                navigateToPostDetail = { postType, postId ->

                },
                navigateToCourseList = {
                    navController.navigate(MainRoute.CourseList.uri())
                },
                navigateToPostEdit = { postType, postId ->
                    navController.navigate(MainRoute.PostEdit.uri(postType.name, postId))
                },
                navigateToProfileDetail = {
                    navController.navigate(MainRoute.StudentProfileDetail.uri())
                },
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
                        Menu.EXAM_ROUTINE -> {
                        }
                        Menu.FEES_SCHEDULE -> {
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
        composable(MainRoute.TeacherDetail.route) { backStack ->
            val teacherId = MainRoute.TeacherDetail.getArgument(backStack)

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
        composable(MainRoute.StudentProfileDetail.route) { _ ->
            StudentDetailScreen(
                viewModel = hiltViewModel(),
                navigateToOnBoarding = {
                    navController.navigate(MainRoute.Auth.route) {
                        popUpTo(MainRoute.Auth.route) {
                            inclusive = true
                        }
                    }
                },
                navigateToProfileEdit = {
                    navController.navigate(MainRoute.StudentProfileEdit.uri("backPress"))
                },
                navigateToSectionDetail = {
                    navController.navigate(MainRoute.SectionDetail.uri(it))
                },
                onBackPress = navController::navigateUp
            )
        }
        // endregion

        // region: CourseList
        composable(MainRoute.CourseList.route) {
            CourseListScreen(
                viewModel = hiltViewModel(),
                navigateToSectionList = { navController.navigate(MainRoute.SectionList.uri(it)) },
                onBackPress = navController::navigateUp
            )
        }
        // endregion

        //region: SectionEdit
        composable(MainRoute.SectionEdit.route) {
            val arg = MainRoute.SectionEdit.getArgument(it)

            SectionEditScreen(
                viewModel = hiltViewModel(),
                sectionId = arg.second,
                courseId = arg.first,
                navigateToTeacherEdit = {
                    navController.navigate(MainRoute.TeacherProfileEdit.uri())
                },
                onBackPress = navController::navigateUp
            )
        }
        // endregion

        // region: SectionList
        composable(MainRoute.SectionList.route) { backStack ->
            val courseId = MainRoute.SectionList.getArgument(backStack)

            SectionListScreen(
                viewModel = hiltViewModel(),
                courseId = courseId,
                navigateToSectionDetail = {
                    navController.navigate(MainRoute.SectionDetail.uri(it))
                },
                createNewSection = { navController.navigate(MainRoute.SectionEdit.uri(it)) },
                onBackPress = navController::navigateUp
            )
        }
        // endregion

        // region: SectionDetail
        composable(MainRoute.SectionDetail.route) { backStack ->
            val sectionId = MainRoute.SectionDetail.getArgument(backStack)

            SectionDetailScreen(
                viewModel = hiltViewModel(),
                sectionId = sectionId,
                navigateToSectionEdit = { course, section ->
                    navController.navigate(MainRoute.SectionEdit.uri(course, section))
                },
                navigateToTeacherDetail = {
                    navController.navigate(MainRoute.TeacherDetail.uri(it))
                },
                onBackPress = navController::navigateUp
            )
        }
        // endregion

        // region: PostEdit
        composable(MainRoute.PostEdit.route) { backStack ->
            val arg = MainRoute.PostEdit.getArgument(backStack)
            val postType = PostType.valueOf(arg.first)
            val postId = arg.second

            PostEditScreen(
                viewModel = hiltViewModel(),
                postType = postType,
                postId = postId,
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
    val SectionDetail = SingleArgRoute("SectionDetail", "sectionId")

    val PostEdit = RequiredAndOptionalArgRoute(
        path = "PostEdit",
        requiredArgName = "postType",
        optionArgName = "courseId"
    )
}
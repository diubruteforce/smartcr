package io.github.diubruteforce.smartcr.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.navigation
import androidx.navigation.compose.popUpTo
import io.github.diubruteforce.smartcr.di.hiltViewModel
import io.github.diubruteforce.smartcr.ui.onboading.*

fun NavGraphBuilder.authNavigation(navController: NavHostController, route: String) {
    navigation(
        route = route,
        startDestination = AuthRoute.Splash.route
    ) {
        // region: Splash
        composable(AuthRoute.Splash.route) {
            SplashScreen(
                viewModel = hiltViewModel(),
                navigateToOnBoarding = {
                    navController.navigate(AuthRoute.SignIn.uri()) {
                        popUpTo(AuthRoute.Splash.route) { inclusive = true }
                    }
                },
                navigateToVerification = {
                    navController.navigate(AuthRoute.Verification.uri(it)) {
                        popUpTo(AuthRoute.Splash.route) { inclusive = true }
                    }
                },
                navigateToProfileEdit = {
                    navController.navigate(MainRoute.StudentProfileEdit.uri()) {
                        popUpTo(AuthRoute.Splash.route) { inclusive = true }
                    }
                },
                navigateToHome = {
                    navController.navigate(MainRoute.SmartCR.uri()) {
                        popUpTo(AuthRoute.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        // endregion

        // region: SignIn
        composable(AuthRoute.SignIn.route) {
            SignInScreen(
                viewModel = hiltViewModel(),
                navigateToVerification = {
                    navController.navigate(AuthRoute.Verification.uri(it)) {
                        popUpTo(AuthRoute.SignIn.route) { inclusive = true }
                    }
                },
                navigateToProfileEdit = {
                    navController.navigate(MainRoute.StudentProfileEdit.uri()) {
                        popUpTo(AuthRoute.SignIn.route) { inclusive = true }
                    }
                },
                navigateToHome = {
                    navController.navigate(MainRoute.SmartCR.uri()) {
                        popUpTo(AuthRoute.SignIn.route) { inclusive = true }
                    }
                },
                navigateToForgotPassword = { navController.navigate(AuthRoute.ForgotPassword.uri()) },
                navigateToSignUp = { navController.navigate(AuthRoute.SignUp.uri()) }
            )
        }
        // endregion

        // region: SignUp
        composable(AuthRoute.SignUp.route) {
            SignUpScreen(
                viewModel = hiltViewModel(),
                navigateToSignIn = {
                    navController.navigate(AuthRoute.SignIn.uri()) {
                        popUpTo(AuthRoute.SignIn.route) { inclusive = true }
                    }
                },
                navigateToEmailVerification = {
                    navController.navigate(AuthRoute.Verification.uri(it)) {
                        popUpTo(AuthRoute.SignIn.route) { inclusive = true }
                    }
                }
            )
        }
        // endregion

        // region: Verification
        composable(AuthRoute.Verification.route) { backStackEntry ->
            val email = AuthRoute.Verification.getArgument(backStackEntry)

            VerificationScreen(
                email = email,
                viewModel = hiltViewModel(),
                navigateToSignIn = {
                    navController.navigate(AuthRoute.SignIn.uri()) {
                        popUpTo(AuthRoute.Verification.route) { inclusive = true }
                    }
                }
            )
        }
        // endregion

        // region: ForgotPassword
        composable(AuthRoute.ForgotPassword.route) {
            ForgotScreen(
                viewModel = hiltViewModel(),
                navigateToSignIn = {
                    navController.navigate(AuthRoute.SignIn.uri()) {
                        popUpTo(AuthRoute.ForgotPassword.route) { inclusive = true }
                    }
                }
            )
        }
        // endregion
    }
}

object AuthRoute {
    val Splash = NoArgRoute("Splash")
    val SignUp = NoArgRoute("SignUp")
    val SignIn = NoArgRoute("SignIn")
    val Verification = SingleArgRoute("Verification", "email")
    val ForgotPassword = NoArgRoute("ForgotPassword")
}
package io.github.diubruteforce.smartcr

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.AmbientLifecycleOwner
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.viewinterop.viewModel
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.*
import androidx.savedstate.SavedStateRegistryOwner
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import io.github.diubruteforce.smartcr.di.AppSavedStateViewModelFactory
import io.github.diubruteforce.smartcr.di.ViewModelAssistedFactoryMap
import io.github.diubruteforce.smartcr.ui.home.HomeScreen
import io.github.diubruteforce.smartcr.ui.onboading.*
import io.github.diubruteforce.smartcr.ui.profile.student.StudentEditScreen
import io.github.diubruteforce.smartcr.ui.theme.SmartCRTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SmartCRTheme {
                ProvideWindowInsets {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Route.Splash
                    ) {
                        // region: OnBoarding Screens
                        composable(Route.Splash) {
                            SplashScreen(
                                viewModel = hiltViewModel(),
                                navigateToOnBoarding = {
                                    navController.navigate(Route.SignIn) {
                                        popUpTo(Route.Splash) { inclusive = true }
                                    }
                                },
                                navigateToVerification = {
                                    navController.navigate(Route.verificationRoute(it)) {
                                        popUpTo(Route.Splash) { inclusive = true }
                                    }
                                },
                                navigateToProfileEdit = {
                                    navController.navigate(Route.StudentProfileEdit) {
                                        popUpTo(Route.Splash) { inclusive = true }
                                    }
                                },
                                navigateToHome = {
                                    navController.navigate(Route.Home) {
                                        popUpTo(Route.Splash) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Route.SignIn) {
                            SignInScreen(
                                viewModel = hiltViewModel(),
                                navigateToVerification = {
                                    navController.navigate(Route.verificationRoute(it)) {
                                        popUpTo(Route.SignIn) { inclusive = true }
                                    }
                                },
                                navigateToProfileEdit = {
                                    navController.navigate(Route.StudentProfileEdit) {
                                        popUpTo(Route.SignIn) { inclusive = true }
                                    }
                                },
                                navigateToHome = {
                                    navController.navigate(Route.Home) {
                                        popUpTo(Route.SignIn) { inclusive = true }
                                    }
                                },
                                navigateToForgotPassword = { navController.navigate(Route.ForgotPassword) },
                                navigateToSignUp = { navController.navigate(Route.SignUp) }
                            )
                        }

                        composable(Route.SignUp) {
                            SignUpScreen(
                                viewModel = hiltViewModel(),
                                navigateToSignIn = {
                                    navController.navigate(Route.SignIn) {
                                        popUpTo(Route.SignIn) { inclusive = true }
                                    }
                                },
                                navigateToEmailVerification = {
                                    navController.navigate(Route.verificationRoute(it)) {
                                        popUpTo(Route.SignIn) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Route.Verification) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email")!!

                            VerificationScreen(
                                email = email,
                                viewModel = hiltViewModel(),
                                navigateToSignIn = {
                                    navController.navigate(Route.SignIn) {
                                        popUpTo(Route.Verification) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(Route.ForgotPassword) {
                            ForgotScreen(
                                viewModel = hiltViewModel(),
                                navigateToSignIn = {
                                    navController.navigate(Route.SignIn) {
                                        popUpTo(Route.ForgotPassword) { inclusive = true }
                                    }
                                }
                            )
                        }
                        // endregion

                        composable(Route.Home) {
                            HomeScreen(navigateToOnBoarding = { navController.navigate(Route.SignIn) })
                        }

                        // region: Student Profile
                        composable(Route.StudentProfileEdit) {
                            StudentEditScreen(
                                viewModel = hiltViewModel(),
                                onBackPress = null,
                                onNavigateToHome = {
                                    navController.navigate(Route.Home) { launchSingleTop = true }
                                }
                            )
                        }
                        // endregion
                    }

                }
            }
        }
    }

    // region: Hilt+ComposeNavigation Integration
    /*
    * Currently it is not possible to use both Hilt and
    * NavGraph viewModel() at a time to get ViewModel
    *
    * This is an workaround to solve the problem
    */
    @Inject
    lateinit var viewModelAssistedFactories: ViewModelAssistedFactoryMap

    @Composable
    private inline fun <reified VM : ViewModel> hiltViewModel(): VM {
        val savedStateRegistryOwner = AmbientLifecycleOwner.current as SavedStateRegistryOwner

        val factory = remember {
            AppSavedStateViewModelFactory(savedStateRegistryOwner, viewModelAssistedFactories)
        }

        return viewModel(factory = factory)
    }
    // endregion


    /*
    * This method is called by fragment to get the theme
    * By changing this theme I am changing the splash theme
    * In Splash theme I have green background for the window
    * copied from https://stackoverflow.com/a/39150319/6307259
    * */
    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        theme.applyStyle(R.style.Theme_SmartCR_NoActionBar, true)

        return theme
    }
}

object Route {
    const val Splash = "Splash"
    const val SignUp = "SignUp"
    const val SignIn = "SignIn"
    const val Verification = "verification/{email}"
    fun verificationRoute(email: String) = "verification/$email"
    const val ForgotPassword = "ForgotPassword"

    const val Home = "Home"

    const val StudentProfileEdit = "StudentProfileEdit"
}
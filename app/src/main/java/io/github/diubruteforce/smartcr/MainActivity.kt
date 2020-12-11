package io.github.diubruteforce.smartcr

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.viewinterop.viewModel
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import io.github.diubruteforce.smartcr.ui.onboading.SignInScreen
import io.github.diubruteforce.smartcr.ui.onboading.SignInViewModel
import io.github.diubruteforce.smartcr.ui.onboading.SplashScreen
import io.github.diubruteforce.smartcr.ui.theme.SmartCRTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SmartCRTheme {
                ProvideWindowInsets {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = Route.SignIn) {
                        composable(Route.Splash) {
                            SplashScreen()
                        }

                        composable(Route.SignIn) {
                            SignInScreen(
                                viewModel = hiltViewModel(),
                                navigateToHome = { },
                                navigateToForgotPassword = { },
                                navigateToSignUp = { }
                            )
                        }
                    }

                }
            }
        }
    }

    @Composable
    inline fun <reified VM : ViewModel> hiltViewModel() =
        viewModel<VM>(factory = defaultViewModelProviderFactory)

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
}
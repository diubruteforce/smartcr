package io.github.diubruteforce.smartcr.ui.smartcr

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Grading
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.navigation.compose.*
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.ui.common.Empty
import io.github.diubruteforce.smartcr.ui.common.InsetAwareTopAppBar
import io.github.diubruteforce.smartcr.ui.smartcr.home.HomeScreen
import io.github.diubruteforce.smartcr.ui.smartcr.menu.Menu
import io.github.diubruteforce.smartcr.ui.smartcr.menu.MenuScreen

@Composable
fun SmartCRScreen(
    onMenuClick: (Menu) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)

    Scaffold(
        modifier = Modifier.fillMaxSize().navigationBarsWithImePadding(),
        topBar = {
            InsetAwareTopAppBar {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = currentRoute ?: HomeRoute.HOME.route
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = MaterialTheme.colors.surface
            ) {
                HomeRoute.values().forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.imageVector) },
                        label = { Text(screen.route) },
                        selected = currentRoute == screen.route,
                        selectedContentColor = MaterialTheme.colors.primary,
                        unselectedContentColor = Color.Gray,
                        alwaysShowLabels = false,
                        onClick = {
                            navController.popBackStack(navController.graph.startDestination, false)

                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route)
                            }
                        }
                    )
                }
            }
        }
    ) {
        NavHost(navController, startDestination = HomeRoute.HOME.route) {
            composable(HomeRoute.HOME.route) {
                HomeScreen(navigateToOnBoarding = { /*TODO*/ })
            }
            composable(HomeRoute.EVENT.route) {
                Empty(
                    title = "New Semester!",
                    message = "DIU DIU DIU DIU DIU DIU DIU DIU DIU DIU DIU DIU",
                    image = vectorResource(id = R.drawable.new_class),
                    actionTitle = "Join Section",
                    onAction = {}
                )
            }
            composable(HomeRoute.TODO.route) {
                HomeScreen(navigateToOnBoarding = { /*TODO*/ })
            }
            composable(HomeRoute.MENU.route) {
                MenuScreen(onMenuClick = onMenuClick)
            }
        }
    }
}

enum class HomeRoute(val route: String, val imageVector: ImageVector) {
    HOME("Home", Icons.Outlined.Home),
    EVENT("Event", Icons.Outlined.Event),
    TODO("To Do", Icons.Outlined.Grading),
    MENU("Menu", Icons.Outlined.Menu)
}
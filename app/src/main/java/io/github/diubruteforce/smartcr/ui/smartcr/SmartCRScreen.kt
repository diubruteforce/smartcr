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
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.di.hiltViewModel
import io.github.diubruteforce.smartcr.model.data.PostType
import io.github.diubruteforce.smartcr.ui.common.InsetAwareTopAppBar
import io.github.diubruteforce.smartcr.ui.smartcr.home.HomeScreen
import io.github.diubruteforce.smartcr.ui.smartcr.menu.Menu
import io.github.diubruteforce.smartcr.ui.smartcr.menu.MenuScreen

@Composable
fun SmartCRScreen(
    navigateToSectionDetail: (String) -> Unit,
    navigateToPostDetail: (PostType, String) -> Unit,
    navigateToCourseList: () -> Unit,
    navigateToPostEdit: (PostType, String?) -> Unit,
    onMenuClick: (Menu) -> Unit
) {
    var currentScreen by savedInstanceState { HomeRoute.HOME }

    Scaffold(
        modifier = Modifier.fillMaxSize().navigationBarsWithImePadding(),
        topBar = {
            InsetAwareTopAppBar {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = HomeRoute.HOME.route
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
                        selected = currentScreen == screen,
                        selectedContentColor = MaterialTheme.colors.primary,
                        unselectedContentColor = Color.Gray,
                        alwaysShowLabels = true,
                        onClick = { currentScreen = screen }
                    )
                }
            }
        }
    ) {
        when (currentScreen) {
            HomeRoute.HOME -> {
                HomeScreen(
                    viewModel = hiltViewModel(),
                    navigateToSectionDetail = navigateToSectionDetail,
                    navigateToPostDetail = navigateToPostDetail,
                    navigateToCourseList = navigateToCourseList,
                    navigateToPostEdit = { navigateToPostEdit.invoke(it, null) }
                )
            }
            HomeRoute.EVENT -> {

            }
            HomeRoute.TODO -> {

            }
            HomeRoute.MENU -> {
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
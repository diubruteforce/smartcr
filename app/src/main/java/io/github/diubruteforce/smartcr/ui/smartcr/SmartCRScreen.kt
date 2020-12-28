package io.github.diubruteforce.smartcr.ui.smartcr

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import dev.chrisbanes.accompanist.insets.navigationBarsWithImePadding
import io.github.diubruteforce.smartcr.R
import io.github.diubruteforce.smartcr.di.hiltViewModel
import io.github.diubruteforce.smartcr.model.data.PostType
import io.github.diubruteforce.smartcr.ui.common.InsetAwareTopAppBar
import io.github.diubruteforce.smartcr.ui.smartcr.home.HomeScreen
import io.github.diubruteforce.smartcr.ui.smartcr.menu.Menu
import io.github.diubruteforce.smartcr.ui.smartcr.menu.MenuScreen
import io.github.diubruteforce.smartcr.ui.theme.grayText

@Composable
fun SmartCRScreen(
    navigateToSectionDetail: (String) -> Unit,
    navigateToPostDetail: (PostType, String) -> Unit,
    navigateToCourseList: () -> Unit,
    navigateToPostEdit: (PostType, String?) -> Unit,
    navigateToProfileDetail: () -> Unit,
    onMenuClick: (Menu) -> Unit
) {
    var currentScreen by savedInstanceState { HomeRoute.HOME }

    Scaffold(
        modifier = Modifier.fillMaxSize().navigationBarsWithImePadding(),
        topBar = {
            InsetAwareTopAppBar {
                IconButton(onClick = {}) {

                }
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically).weight(1f),
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.h6,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colors.primary
                )

                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onClick = navigateToProfileDetail
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        tint = MaterialTheme.colors.grayText
                    )
                }
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
            HomeRoute.Resource -> {

            }
            HomeRoute.TODO -> {

            }
            HomeRoute.MENU -> {
                MenuScreen(onMenuClick = onMenuClick)
            }
        }
    }
}

private enum class HomeRoute(val route: String, val imageVector: ImageVector) {
    HOME("Home", Icons.Outlined.Home),
    Resource("Resource", Icons.Outlined.Book),
    TODO("To Do", Icons.Outlined.Grading),
    MENU("Menu", Icons.Outlined.Menu)
}
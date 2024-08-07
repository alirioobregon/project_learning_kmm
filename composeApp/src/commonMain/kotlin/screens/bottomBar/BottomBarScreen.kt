package screens.bottomBar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator

class BottomBarScreen : Screen {
    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
//        View()
        TabNavigator(
            HomeTab,
            tabDisposable = {
                TabDisposable(
                    it,
                    listOf(HomeTab, FavoriteTab)
                )
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text(it.current.options.title) }, navigationIcon =
                    { Icon(Icons.Default.ArrowBack, contentDescription = null) }
                    )
                },
                bottomBar = {
                    BottomNavigation {
                        val tabNavigator = LocalTabNavigator.current
                        BottomNavigationItem(
                            selected = tabNavigator.current.key == HomeTab.key,
                            label = { Text(HomeTab.options.title) },
                            icon = { Icon(painter = HomeTab.options.icon!!, contentDescription = null) },
                            onClick = {
                                tabNavigator.current = HomeTab
                            }
                        )

                        BottomNavigationItem(
                            selected = tabNavigator.current.key == FavoriteTab.key,
                            label = { Text(FavoriteTab.options.title) },
                            icon = { Icon(painter = FavoriteTab.options.icon!!, contentDescription = null) },
                            onClick = {
                                tabNavigator.current = FavoriteTab
                            }
                        )
                    }
                }
            ) {}
        }
    }
}

//@Composable
//fun View() {
//    val navigator = LocalNavigator.current
//    MaterialTheme {
//        Column(modifier = Modifier.fillMaxSize()) {
//            Button(
//                onClick = {
//                    navigator?.pop()
//                },
//                modifier = Modifier.align(Alignment.Start)
//            ) {
//                Icon(Icons.Default.ArrowBack, contentDescription = null)
//            }
//        }
//    }
//}
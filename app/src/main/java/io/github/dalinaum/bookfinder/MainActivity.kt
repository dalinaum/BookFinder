package io.github.dalinaum.bookfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.dalinaum.bookfinder.screen.DetailScreen
import io.github.dalinaum.bookfinder.screen.HomeScreen
import io.github.dalinaum.bookfinder.ui.theme.BookFinderTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookFinderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TopLevel()
                }
            }
        }
    }
}

@Composable
fun TopLevel(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val listState = rememberLazyListState()
    NavHost(
        navController = navController,
        startDestination = "Home",
        modifier = modifier
    ) {
        composable(
            route = "Home"
        ) {
            HomeScreen(
                navController = navController,
                listState = listState
            )
        }
        composable("Detail/{id}") { entry ->
            val id = entry.arguments?.getString("id")
                ?: throw IllegalArgumentException("Detail 스크린은 id가 필요합니다.")
            DetailScreen(
                navController = navController,
                id = id
            )
        }
    }
}
package com.example.mylist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mylist.presentation.categories.CategoriesScreen
import com.example.mylist.presentation.categories.CategoriesViewModel
import com.example.mylist.presentation.items.ItemsScreen
import com.example.mylist.presentation.items.ItemsViewModel
import com.example.mylist.ui.theme.MyListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyListTheme {
                MyListApp()
            }
        }
    }
}

@Composable
fun MyListApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "categories") {
        composable("categories") {
            val viewModel: CategoriesViewModel = hiltViewModel()
            CategoriesScreen(
                viewModel = viewModel,
                onCategoryClick = { category ->
                    navController.navigate("items/${category.id}/${category.name}/${category.color}")
                }
            )
        }
        composable(
            route = "items/{categoryId}/{categoryName}/{categoryColor}",
            arguments = listOf(
                navArgument("categoryId") { type = NavType.LongType },
                navArgument("categoryName") { type = NavType.StringType },
                navArgument("categoryColor") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            val categoryColor = backStackEntry.arguments?.getInt("categoryColor") ?: 0
            val viewModel: ItemsViewModel = hiltViewModel()
            ItemsScreen(
                viewModel = viewModel,
                categoryName = categoryName,
                categoryColor = categoryColor,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
package com.kin.easynotes.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kin.easynotes.presentation.screens.home.HomeView


@Composable
fun AppNavHost( startDestination : String, navController: NavHostController = rememberNavController()) {
    NavHost(navController, startDestination = startDestination) {
        animatedComposable(route = NavRoutes.Home.route) {
            HomeView(navController = navController)
        }
    }
}
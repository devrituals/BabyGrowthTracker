package com.example.babygrowthtracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.babygrowthtracker.ui.auth.AuthScreen
import com.example.babygrowthtracker.ui.dashboard.DashboardScreen
import com.example.babygrowthtracker.ui.growth.GrowthScreen
import com.example.babygrowthtracker.ui.guide.GuideScreen
import com.example.babygrowthtracker.ui.onboarding.OnboardingScreen
import com.example.babygrowthtracker.ui.paywall.PaywallScreen
import com.example.babygrowthtracker.ui.profile.CreateProfileScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "onboarding") {

        composable("onboarding") {
            OnboardingScreen(onFinished = {
                navController.navigate("auth") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }

        composable("auth") {
            AuthScreen(onSignInSuccess = {
                navController.navigate("create_profile") {
                    popUpTo("auth") { inclusive = true }
                }
            })
        }

        composable("create_profile") {
            CreateProfileScreen(onProfileCreated = {
                navController.navigate("dashboard") {
                    popUpTo("create_profile") { inclusive = true }
                }
            })
        }

        composable("dashboard") {
            DashboardScreen(
                onNavigateToGrowthChart = {
                    navController.navigate("growth")
                },
                onNavigateToGuide = {
                    navController.navigate("guide")
                }
            )
        }

        composable("growth") {
            GrowthScreen(
                onNavigateToPaywall = {
                    navController.navigate("paywall")
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("guide") {
            GuideScreen(onBackClick = { navController.popBackStack() })
        }

        composable("paywall") {
            PaywallScreen()
        }
    }
}
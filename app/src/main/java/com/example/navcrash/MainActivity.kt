package com.example.navcrash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController

object Routes {
    data object RouteA {
        const val NAME: String = "main"
        const val SCREEN_A_1 = "screen_A_1"
        const val SCREEN_A_2 = "screen_A_2"
    }

    data object RouteB {
        const val name: String = "root_B"
        const val SCREEN_B_1 = "screen_B_1"
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Routes.RouteA.NAME,
            ) {
                navigation(
                    route = Routes.RouteA.NAME,
                    startDestination = Routes.RouteA.SCREEN_A_1,
                ) {
                    composable(route = Routes.RouteA.SCREEN_A_1) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            /*
                             * step1: Set breakpoint in `androidx.navigation.NavController#pop`.
                             * step2: Click this button.
                             */
                            Button(
                                onClick = {
                                    navController.navigate(Routes.RouteA.SCREEN_A_2)
                                }
                            ) {
                                Text(text = "navigate")
                            }
                        }
                    }
                    composable(route = Routes.RouteA.SCREEN_A_2) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            Text(Routes.RouteA.SCREEN_A_2)
                        }
                        LaunchedEffect(Unit) {
                            /*
                             * step3: Wait for stop at breakpoint `androidx.navigation.NavController#pop`.
                             * step4: After stopping at a breakpoint, press the back key.
                             */
                            navController.navigate(Routes.RouteB.name) {
                                popUpTo(Routes.RouteA.SCREEN_A_1)
                            }
                        }
                    }
                }
                navigation(
                    route = Routes.RouteB.name,
                    startDestination = Routes.RouteB.SCREEN_B_1,
                ) {
                    composable(route = Routes.RouteB.SCREEN_B_1) {
                        /*
                        * Resume the program after performing step 4, it crashes here.
                        * (java.lang.IllegalArgumentException: No destination with route root_B is on the NavController's back stack. The current destination is Destination(0x8efb6cad) route=screen_A_1)
                        */
                        val parentEntry =
                            remember(it) { navController.getBackStackEntry(Routes.RouteB.name) }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize(),
                        ) {
                            Text("${parentEntry.destination}")
                        }
                    }
                }
            }
        }
    }
}

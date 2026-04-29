package com.robert.mymarketplace.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Listing : Screen("listing")
    object AddEdit : Screen("add_edit")
    object Detail : Screen("detail/{id}") {
        fun createRoute(id: String) = "detail/$id"
    }
}
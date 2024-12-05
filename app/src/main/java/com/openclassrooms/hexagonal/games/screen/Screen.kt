package com.openclassrooms.hexagonal.games.screen

import androidx.navigation.NamedNavArgument
import androidx.navigation.navArgument

sealed class Screen(
  val route: String,
  val navArguments: List<NamedNavArgument> = emptyList()
) {
  data object Homefeed : Screen("homefeed")
  
  data object AddPost : Screen("addPost")
  
  data object Settings : Screen("settings")

  //add login
  data object Login : Screen("login")

  //account page
  data object Account : Screen("account")

  //detail page
  data object Detail : Screen(
    route = "detail",
    navArguments = listOf(navArgument("postId") {
      nullable = false
    })
  )
}
package com.openclassrooms.hexagonal.games.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.hexagonal.games.screen.Screen
import com.openclassrooms.hexagonal.games.screen.account.AccountScreen
import com.openclassrooms.hexagonal.games.screen.ad.AddScreen
import com.openclassrooms.hexagonal.games.screen.comment.CommentScreen
import com.openclassrooms.hexagonal.games.screen.detail.DetailScreen
import com.openclassrooms.hexagonal.games.screen.homefeed.HomefeedScreen
import com.openclassrooms.hexagonal.games.screen.login.LoginScreen
import com.openclassrooms.hexagonal.games.screen.settings.SettingsScreen
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main activity for the application. This activity serves as the entry point and container for the navigation
 * fragment. It handles setting up the toolbar, navigation controller, and action bar behavior.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    setContent {
      val navController = rememberNavController()
      
      HexagonalGamesTheme {
        HexagonalGamesNavHost(navHostController = navController)
      }
    }
  }
  
}

//start route sur login
@Composable
fun HexagonalGamesNavHost(navHostController: NavHostController) {
  NavHost(
    navController = navHostController,
    startDestination = Screen.Login.route
  ) {
    composable(route = Screen.Login.route) {
      LoginScreen(
        onLoginSuccess = {
          navHostController.navigate(Screen.Homefeed.route) {
            popUpTo(Screen.Login.route) { inclusive = true }
          }
        }
      )
    }
    composable(route = Screen.Homefeed.route) {
      HomefeedScreen(
        onPostClick = { post ->
          navHostController.navigate("detail/${post.id}")
        },
        onSettingsClick = {
          navHostController.navigate(Screen.Settings.route)
        },
        onFABClick = {
          navHostController.navigate(Screen.AddPost.route)
        },
        //add route vers accountScreen
        onAccountClick = {
          navHostController.navigate(Screen.Account.route)
        }
      )
    }
    //add de la composante Route account
    composable(route = Screen.Account.route) {
      AccountScreen(
        onLogoutSuccess = {
          navHostController.navigate(Screen.Login.route) {
            popUpTo(Screen.Homefeed.route) { inclusive = true }
          }
        },
        onDeleteSuccess = {
          navHostController.navigate(Screen.Login.route) {
            popUpTo(Screen.Homefeed.route) { inclusive = true }
          }
        },
        onBackClick = { navHostController.navigateUp() }
      )
    }
    composable(route = Screen.AddPost.route) {
      AddScreen(
        onBackClick = { navHostController.navigateUp() },
        onSaveClick = { navHostController.navigateUp() }
      )
    }
    composable(route = Screen.Settings.route) {
      SettingsScreen(
        onBackClick = { navHostController.navigateUp() }
      )
    }
    composable("detail/{postId}") { backStackEntry ->
            //recup argument du postId
      val postId = backStackEntry.arguments?.getString("postId")
      requireNotNull(postId) { "postId param not found" }
      DetailScreen(postId = postId, onBackClick = { navHostController.navigateUp() },
        onNavigateToComment = { postId ->
          navHostController.navigate("comment/$postId")
        })
    }
    composable("comment/{postId}") { backStackEntry ->
      val postId = backStackEntry.arguments?.getString("postId") ?: ""
      CommentScreen(
        postId = postId,
        onBack = { navHostController.popBackStack() }
      )
    }
  }
}

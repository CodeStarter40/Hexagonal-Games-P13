package com.openclassrooms.hexagonal.games.screen.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging

/**
 * ViewModel responsible for managing user settings, specifically notification preferences.
 */
class SettingsViewModel : ViewModel() {
  /**
   * Enables notifications for the application.
   * TODO: Implement the logic to enable notifications, likely involving interactions with a notification manager.
   */
  fun enableNotifications() {
    FirebaseMessaging.getInstance().subscribeToTopic("notifications")
      .addOnCompleteListener{ task ->
        if (task.isSuccessful) {
          Log.d("Notifications", "Notifications enabled")
        } else {
          Log.d("Notifications", "Notifications not enabled", task.exception)
        }
      }
  }
  
  /**
   * Disables notifications for the application.
   * TODO: Implement the logic to disable notifications, likely involving interactions with a notification manager.
   */
  fun disableNotifications() {
    FirebaseMessaging.getInstance().unsubscribeFromTopic("notifications")
      .addOnCompleteListener{ task ->
        if (task.isSuccessful) {
          Log.d("Notifications", "Notifications disabled")
        } else {
          Log.d("Notifications", "Notifications not disabled", task.exception)
        }
        }
  }
  
}

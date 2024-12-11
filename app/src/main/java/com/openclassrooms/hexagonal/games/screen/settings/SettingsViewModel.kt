package com.openclassrooms.hexagonal.games.screen.settings

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.messaging.FirebaseMessaging

/**
 * ViewModel responsible for managing user settings, specifically notification preferences.
 */
class SettingsViewModel : ViewModel() {

  private val _openNotificationSettingsEvent = MutableLiveData<Unit>()
  val openNotificationSettingsEvent: LiveData<Unit> get () = _openNotificationSettingsEvent

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
      .addOnCompleteListener { task ->
        if (task.isSuccessful) {
          Log.d("Notifications", "Notifications disabled")
          _openNotificationSettingsEvent.value = Unit
        } else {
          Log.d("Notifications", "Notifications not disabled", task.exception)
        }
      }
  }
}

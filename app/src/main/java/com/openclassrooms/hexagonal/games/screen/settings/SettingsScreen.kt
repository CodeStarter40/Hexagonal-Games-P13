package com.openclassrooms.hexagonal.games.screen.settings

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.ui.theme.HexagonalGamesTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  modifier: Modifier = Modifier,
  viewModel: SettingsViewModel = hiltViewModel(),
  onBackClick: () -> Unit
) {
  val context = LocalContext.current
  val openNotificationSettingsEvent = viewModel.openNotificationSettingsEvent.observeAsState()
  openNotificationSettingsEvent.value?.let {
    openNotificationSettings(context)
  }

  Scaffold(
    modifier = modifier,
    topBar = {
      TopAppBar(
        title = {
          Text(stringResource(id = R.string.action_settings))
        },
        navigationIcon = {
          IconButton(onClick = {
            onBackClick()
          }) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = stringResource(id = R.string.contentDescription_go_back)
            )
          }
        }
      )
    }
  ) { contentPadding ->
    Settings(
      modifier = Modifier.padding(contentPadding),
      onNotificationDisabledClicked = { viewModel.disableNotifications() },
      onNotificationEnabledClicked = { viewModel.enableNotifications() }
    )
  }
}
fun openNotificationSettings(context: Context) {
  val intent = Intent().apply {
    action = android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
    putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context.packageName)
  }
  context.startActivity(intent)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun Settings(
  modifier: Modifier = Modifier,
  onNotificationEnabledClicked: () -> Unit,
  onNotificationDisabledClicked: () -> Unit
) {
  val context = LocalContext.current
  val notificationsPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    rememberPermissionState(
      android.Manifest.permission.POST_NOTIFICATIONS
    )
  } else {
    null
  }
  
  Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceEvenly
  ) {
    Icon(
      modifier = Modifier.size(200.dp),
      painter = painterResource(id = R.drawable.ic_notifications),
      tint = MaterialTheme.colorScheme.onSurface,
      contentDescription = stringResource(id = R.string.contentDescription_notification_icon)
    )
    Button(
      onClick = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          if (notificationsPermissionState?.status?.isGranted == false) {
            notificationsPermissionState.launchPermissionRequest()
          }
        }
        
        onNotificationEnabledClicked()
        Toast.makeText(context, "Notifications enabled", Toast.LENGTH_SHORT).show()
      }
    ) {
      Text(text = stringResource(id = R.string.notification_enable))
    }
    Button(
      onClick = {
        onNotificationDisabledClicked()
        Toast.makeText(context, "Notifications disabled", Toast.LENGTH_SHORT).show()}
    ) {
      Text(text = stringResource(id = R.string.notification_disable))
    }
  }
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun SettingsPreview() {
  HexagonalGamesTheme {
    Settings(
      onNotificationEnabledClicked = { },
      onNotificationDisabledClicked = { }
    )
  }
}